package org.mkcl.els.controller.cis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeDesignation;
import org.mkcl.els.domain.CommitteeMember;
import org.mkcl.els.domain.CommitteeMemberAttendance;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	
	@RequestMapping(value ="/adminCommitteeCreation/module",method = RequestMethod.GET)
	public String  getCommitteeCreationModule(final ModelMap map,HttpServletRequest request,HttpSession session ,final Locale locale) {
		
		
		String urlPattern=request.getRequestURI().toString();
		System.out.println(urlPattern);
		
		String[] SplittedURI = urlPattern.split("/");
		map.addAttribute("urlPattern", SplittedURI[3]);
		map.addAttribute("messagePattern", SplittedURI[2]);
		
//         String messagePattern=urlPattern.replaceAll("\\/",".");
//         model.addAttribute("messagePattern", messagePattern);
//         model.addAttribute("urlPattern", urlPattern);
		return "committee/committeeCreation/module";
	}
	
	@RequestMapping(value ="/adminCommitteeCreation/list",method = RequestMethod.GET)
	public String  getCommitteeCreationList(final ModelMap map,HttpServletRequest request,final Locale locale) {
		
		
		String urlPatternFromRequest=request.getRequestURI().toString();
		System.out.println(urlPatternFromRequest);
		
		String[] SplittedURI = urlPatternFromRequest.split("/");
		map.addAttribute("urlPattern", SplittedURI[3]);
		map.addAttribute("messagePattern", SplittedURI[2]);
		
		// final String servletPath = request.getServletPath().replaceFirst("\\/","");
	        String urlPattern=SplittedURI[3];
	        String messagePattern=SplittedURI[2];
	       // String newurlPattern=modifyURLPattern(urlPattern,request,map,locale.toString());
	        Grid grid = null;
	        try{
	        	grid = Grid.findByDetailView(urlPattern, locale.toString());
	        	map.addAttribute("gridId", grid.getId());
	        }catch (ELSException e) {
	        	logger.error(e.getMessage());
				map.addAttribute("error", e.getParameter());			
			}        
//	        /******* Hook **********/
//	        populateList(model, request, locale.toString(), this.getCurrentUser());
//	        /***********************/
//	        if (formtype != null) {
//	            if (formtype.equals("g")) {
//	                model.addAttribute("messagePattern", messagePattern);
//	                model.addAttribute("urlPattern", urlPattern);
//	                return "generic/list";
//	            }
//	        }
	        //here making provisions for displaying error pages
	        if(map.containsAttribute("errorcode")){
	            return "generic/error";
	        }else{
	        	return "committee/committeeCreation/list";
	        }
	
	}
	
	@RequestMapping(value ="/adminCommitteeCreation/new",method = RequestMethod.GET)
	public String  getCommitteeCreationNew(final ModelMap model,HttpServletRequest request,final Locale locale) {
		
		List<CommitteeName> committeeNames = CommitteeName.findAll(CommitteeName.class, "id", ApplicationConstants.DESC, locale.toString());
		model.addAttribute("committeeNames", committeeNames);
		
		List<Status> allCommitteStatus = Status.findAllByStartingWith(Status.class, "type", "committee", "id",ApplicationConstants.ASC, locale.toString());
		model.addAttribute("allCommitteeStatus", allCommitteStatus);
		
		List<CommitteeDesignation> committeeDesignation = CommitteeDesignation.findAll(CommitteeDesignation.class, "id", ApplicationConstants.DESC, locale.toString());
		model.addAttribute("committeeDesignation",committeeDesignation);
		
		
		return "committee/committeeCreation/new";
	}
	
	
	@RequestMapping(value ="/adminCommitteeCreation/create",method = RequestMethod.POST)
	public String  getCommitteeCreationCreate(final ModelMap model,HttpServletRequest request,final Locale locale) {
		String retVal = "generic/error";
		
		
		String strtotalRows = request.getParameter("totalRows");
		String strcommitteeNameId = request.getParameter("committeeNameId");
		String strStatusId = request.getParameter("statusId");
		String strFormationDate = request.getParameter("formationDate");
		String strfoundationDate = request.getParameter("foundationDate");
		String strdissolutionDate = request.getParameter("dissolutionDate");
		Long committeeId = null; // populate After Persisting
		
		List<Map<String,String>> committeeMemberDetails = new ArrayList<Map<String,String>>();
		
		Integer totalRows = Integer.parseInt(strtotalRows);
		
		if(totalRows != null) {
			CommitteeName cm = CommitteeName.findById(CommitteeName.class, Long.parseLong(strcommitteeNameId));
			Status s  = Status.findById(Status.class, Long.parseLong(strStatusId));
						
			for (int i=0;i<totalRows;i++)
			{
				Map<String,String> ymap = new HashMap<String,String>();
				String  priority = request.getParameter("committeeDetails["+i+"][priority]");
				ymap.put("priority",priority);
				
				String  memberId = request.getParameter("committeeDetails["+i+"][memberId]");
				ymap.put("memberId",memberId);
				
				String  designation = request.getParameter("committeeDetails["+i+"][designation]");
				ymap.put("designation",designation);
				
				committeeMemberDetails.add(ymap);
			}
			
			Committee newComittee = new Committee();
			newComittee.setCommitteeName(cm);
			newComittee.setStatus(s);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD"); 
			Date formationDate = null;
			Date foundationDate = null;
			Date dissolutionDate = null;
			try {
				 formationDate = (Date)formatter.parse(strFormationDate);
				 foundationDate = (Date)formatter.parse(strfoundationDate);
				 dissolutionDate = (Date)formatter.parse(strdissolutionDate);
				 
			} catch (ParseException e) {
				System.out.println("Error At Converting String to Date Object in Committee Creation Class in [ getCommitteeCreationCreate ] Method");
				e.printStackTrace();
			}
			
			if(formationDate != null ) {
				newComittee.setFormationDate(formationDate);			
			}else {
				newComittee.setFormationDate(new Date());
			}
			
			 if (foundationDate != null) {
				 newComittee.setCreationDate(foundationDate);
			 }else {
				 newComittee.setCreationDate(new Date());
			 }
			 
			 if(dissolutionDate != null) {
				 newComittee.setDissolutionDate(dissolutionDate);
			 }
		
			List<CommitteeMember> ogCommitteeMember = new ArrayList<CommitteeMember>();
			List<CommitteeMember> invitedCommitteeMember = new ArrayList<CommitteeMember>();
			
			if(committeeMemberDetails != null )
			{
				for(Map<String,String>  i : committeeMemberDetails)
				{
				CommitteeMember committeeMember = new CommitteeMember();
					
				 String strmemberId = 	i.get("memberId");
				 Member member  = Member.findById(Member.class, Long.parseLong(strmemberId));				 
				 committeeMember.setMember(member);				 
				 committeeMember.setJoiningDate(new Date());
				 
				 String strpriority = i.get("priority");
				 if(strpriority != null && !strpriority.equals(" - ")) {
					 committeeMember.setPosition(Integer.parseInt(strpriority));
				 }
				 
				 String strDesignation = i.get("designation");
				 if(strDesignation != null) {
					 CommitteeDesignation memberComitteeDesignation = member.findById(CommitteeDesignation.class, Long.parseLong(strDesignation));
					 committeeMember.setDesignation(memberComitteeDesignation);
				 }		
				 
				 if(committeeMember.getPosition() != null) {
					 ogCommitteeMember.add(committeeMember);
				 }else {
					 invitedCommitteeMember.add(committeeMember);
				 }
				}
			}
			newComittee.setMembers(ogCommitteeMember);
			newComittee.setInvitedMembers(invitedCommitteeMember);
			newComittee.setStatus(s);
			
			int rowsAdded = ogCommitteeMember.size() + invitedCommitteeMember.size();
			
			
			newComittee.setLocale(locale.toString());
			Committee createdCommittee = (Committee) newComittee.persist();
			committeeId = createdCommittee.getId();
									
		}
		
		
			if(committeeId != null) {
				System.out.println("Committee Added SuccessFully !!");
				this.getCommitteeDetailsForEdit(committeeId, model, request, locale);
				model.addAttribute("success","Added");
				retVal = "committee/committeeCreation/edit";		
				return retVal;	
			}								
		return retVal;
	}
	
	@RequestMapping(value ="/adminCommitteeCreation/{id}/edit",method = RequestMethod.GET)
	public String  getCommitteeCreationEdit(final ModelMap model,HttpServletRequest request,@PathVariable("id") final Long committeeId,final Locale locale) {
		this.getCommitteeDetailsForEdit(committeeId,model, request, locale);
		return "committee/committeeCreation/edit";
	}
	
	private void getCommitteeDetailsForEdit( final Long committeeId,final ModelMap model,HttpServletRequest request,final Locale locale) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Committee  selectedCommittee = Committee.findById(Committee.class, committeeId);
		
		model.addAttribute("selectedCommitteeId", selectedCommittee.getId());		
		Status selectedCommitteStatus = selectedCommittee.getStatus();
		model.addAttribute("selectedCommitteStatus",selectedCommitteStatus);
		
		
		CustomParameter cp =CustomParameter.findByFieldName(CustomParameter.class,"name","COMMITTEE_STATUS_FOR_CREATION","");
		
		if(cp != null) {
			
			List<Status> allCommitteeStatus = new ArrayList<Status>();
	 		String allfetchedStatus = cp.getValue();
	 		String[] splitedFetchedStatus = allfetchedStatus.split(",");
			
	 		for(String s : splitedFetchedStatus) {
	 			Status  status = Status.findByType(s, locale.toString());
	 			if(status != null) {
	 				allCommitteeStatus.add(status);
	 			}
	 		}
	 		model.addAttribute("allCommitteeStatus", allCommitteeStatus);
		}
		
		
		List<CommitteeDesignation> allComitteeDesignation = CommitteeDesignation.findAll(CommitteeDesignation.class, "id", ApplicationConstants.ASC, locale.toString());
		model.addAttribute("allComitteeDesignation", allComitteeDesignation);
				
		
		List<CommitteeName> allCommitteeNames = CommitteeName.findAll(CommitteeName.class, "id", ApplicationConstants.DESC, locale.toString());
		model.addAttribute("allCommitteeNames", allCommitteeNames);
		
		model.addAttribute("selectedCommitteeName", selectedCommittee.getCommitteeName().getName());
		
		String strSelectedCommitteFormationDate = null;
		
		if(selectedCommittee.getFormationDate() != null)
		{
			strSelectedCommitteFormationDate = formatter.format(selectedCommittee.getFormationDate());					
		}
		model.addAttribute("selectedCommitteFormationDate", strSelectedCommitteFormationDate);
		
		
		String strSelectedCommitteeCreationDate = null;
		if(selectedCommittee.getCreationDate() != null) {
			strSelectedCommitteeCreationDate = formatter.format(selectedCommittee.getCreationDate());			
		}
		model.addAttribute("selectedCommitteeCreationDate", strSelectedCommitteeCreationDate);
		 
		String strSelectedCommitteeDissolutionDate = null;				
		if(selectedCommittee.getDissolutionDate() != null) {
			strSelectedCommitteeDissolutionDate = formatter.format(selectedCommittee.getDissolutionDate());			
		}
		model.addAttribute("selectedCommitteeDissolutionDate", strSelectedCommitteeDissolutionDate);
		
		List<CommitteeMember> allCommitteeMembers  = new ArrayList<CommitteeMember>();
		allCommitteeMembers.addAll(selectedCommittee.getMembers());
		allCommitteeMembers.addAll(selectedCommittee.getInvitedMembers());
		Collections.sort(allCommitteeMembers,new Comparator<CommitteeMember>() {
			public int compare(CommitteeMember cm1,CommitteeMember cm2) {
				if(cm1.getPosition() == null) {
					return 1;
				}else if(cm2.getPosition() == null) {
					return -1;
				}else if(cm1.getDesignation().getId() == 52 ) {
					return -1;
				}else if(cm2.getDesignation().getId() == 52){
					return -2;
				}else {
					return cm1.getPosition() - cm2.getPosition();
				}
			}});
		
		model.addAttribute("allCommitteeMembers", allCommitteeMembers);		
		model.addAttribute("totalInvitedMembers", selectedCommittee.getInvitedMembers().size());
		model.addAttribute("totalMembers", allCommitteeMembers.size());

		
	}
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value ="/adminCommitteeCreation/edit",method = RequestMethod.POST)
	private  String updateCommitteeDetailsForEdit( final ModelMap model,HttpServletRequest request,final Locale locale) {
		String responseMessage = "error";
		String strtotalRows = request.getParameter("totalRows");
		String strCommitteeId = request.getParameter("committeeId");
		String strcommitteeNameId = request.getParameter("committeeNameId");
		String strStatusId = request.getParameter("statusId");
		String strFormationDate = request.getParameter("formationDate");
		String strfoundationDate = request.getParameter("foundationDate");
		String strdissolutionDate = request.getParameter("dissolutionDate");
		String retVal = "error";
		
		
		Integer totalRows = Integer.parseInt(strtotalRows);
		Committee committeeToBeupdated = Committee.findById(Committee.class, Long.parseLong(strCommitteeId));
		List<Map<String,String>> committeeMemberDetails = new ArrayList<Map<String,String>>();
		
		if(totalRows != null ) {
			CommitteeName cm = CommitteeName.findById(CommitteeName.class, Long.parseLong(strcommitteeNameId));
			Status s  = Status.findById(Status.class, Long.parseLong(strStatusId));
						
			for (int i=0;i<totalRows;i++){
				Map<String,String> ymap = new HashMap<String,String>();
				String  priority = request.getParameter("committeeDetails["+i+"][priority]");
				ymap.put("priority",priority);
				
				String  memberId = request.getParameter("committeeDetails["+i+"][memberId]");
				ymap.put("memberId",memberId);
				
				String  designation = request.getParameter("committeeDetails["+i+"][designation]");
				ymap.put("designation",designation);
				
				committeeMemberDetails.add(ymap);
			}
			
			
			committeeToBeupdated.setCommitteeName(cm);
			committeeToBeupdated.setStatus(s);
			committeeToBeupdated.setLocale(locale.toString());
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD"); 
			Date formationDate = null;
			Date foundationDate = null;
			Date dissolutionDate = null;
			try {
				 formationDate = (Date)formatter.parse(strFormationDate);
				 foundationDate = (Date)formatter.parse(strFormationDate);
				 dissolutionDate = (Date)formatter.parse(strdissolutionDate);
				 
			} catch (ParseException e) {
				System.out.println("Error At Converting String to Date Object in Committee Creation Class in [ updateCommitteeDetailsForEdit ] Method");
				e.printStackTrace();
			}
			
			if(formationDate != null ) {
				committeeToBeupdated.setFormationDate(formationDate);			
			}else {
				committeeToBeupdated.setFormationDate(new Date());
			}
			
			 if (foundationDate != null) {
				 committeeToBeupdated.setCreationDate(foundationDate);
			 }else {
				 committeeToBeupdated.setCreationDate(new Date());
			 }
			 
			 if(dissolutionDate != null) {
				 committeeToBeupdated.setDissolutionDate(dissolutionDate);
			 }
		
			List<CommitteeMember> ogCommitteeMember = new ArrayList<CommitteeMember>();
			List<CommitteeMember> invitedCommitteeMember = new ArrayList<CommitteeMember>();
				
			if(committeeMemberDetails != null ){
				for(Map<String,String>  i : committeeMemberDetails)
				{
				CommitteeMember committeeMember = new CommitteeMember();
					
				 String strmemberId = 	i.get("memberId");
				 Member member  = Member.findById(Member.class, Long.parseLong(strmemberId));				 
				 committeeMember.setMember(member);				 
				 committeeMember.setJoiningDate(new Date());
				 
				 String strpriority = i.get("priority");
				 if(strpriority != null && !strpriority.equals(" - ")) {
					 committeeMember.setPosition(Integer.parseInt(strpriority));
				 }
				 
				 String strDesignation = i.get("designation");
				 if(strDesignation != null) {
					 CommitteeDesignation memberComitteeDesignation = member.findById(CommitteeDesignation.class, Long.parseLong(strDesignation));
					 committeeMember.setDesignation(memberComitteeDesignation);
				 }		
				 
				 if(committeeMember.getPosition() != null) {
					 ogCommitteeMember.add(committeeMember);
				 }else {
					 invitedCommitteeMember.add(committeeMember);
				 }
				}
			}else {
				return retVal;
			}
			
			
			committeeToBeupdated.setMembers(ogCommitteeMember);
			committeeToBeupdated.setInvitedMembers(invitedCommitteeMember);			
			
			int rowsAdded = ogCommitteeMember.size() + invitedCommitteeMember.size();			
			
			committeeToBeupdated.merge();			 
			System.out.println("Committee Updated SuccessFully !!");
			
			model.addAttribute("success", "Updated");
			this.getCommitteeCreationEdit(model, request, Long.parseLong(strCommitteeId), locale);
			
			retVal =  "Updated";
			return "committee/committeeCreation/edit";
		}
		
		return retVal;
	}
	

}