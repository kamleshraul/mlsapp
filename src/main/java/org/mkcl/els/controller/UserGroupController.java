package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user/usergroup")
public class UserGroupController extends GenericController<UserGroup>{

	@Override
	protected void populateList(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		/*
		 * setting credential
		 */
		String strUser=request.getParameter("user");
		model.addAttribute("user",strUser);
		Long userId=Long.parseLong(strUser);
		User user=User.findById(User.class, userId);
		Credential credential=user.getCredential();
		if(credential!=null){
			model.addAttribute("credential",credential.getId());
		}

		/*-------- Edited By Shubham A*/
    	model.addAttribute("userFirstLastName", user.findFirstLastName());
    	model.addAttribute("username", user.getCredential().getUsername());
    	/*--------*/
	}

	@Override
	protected void populateNew(final ModelMap model, final UserGroup domain, final String locale,
			final HttpServletRequest request) {
		try{
			/**** Credential ****/
			String strCredential=request.getParameter("credential");
			Credential credential=Credential.findById(Credential.class,Long.parseLong(strCredential));
			domain.setCredential(credential);
			/****Allowed Groups ****/
			User user  = User.findByUserName(credential.getUsername(), locale);
			if(user != null){
				model.addAttribute("groupsAllowed", user.getGroupsAllowed());			
				model.addAttribute("fullName",user.findFullName());	
				model.addAttribute("userFirstName", user.getFirstName());
				model.addAttribute("userMiddleName", user.getMiddleName());
        		model.addAttribute("userLastName", user.getLastName());
			}
			
			/**** User Group Types ****/
			List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
			model.addAttribute("userGroupTypes",userGroupTypes);
			/**** House Types and Selected House Type ****/
			List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
			model.addAttribute("housetypes",houseTypes);		
			/**** Ministry ****/
			List<Ministry> ministries=Ministry.findAssignedMinistries(locale);
			model.addAttribute("ministries",ministries);		
			/**** Device Types ****/
			List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, locale);
			model.addAttribute("deviceTypes", deviceTypes);
			
			/*** To get the sates for an actor ***/
			CustomParameter csptActorStates = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ACTOR_STATES, "");
			List<Reference> refs = new ArrayList<Reference>();			
			if(csptActorStates != null){
				if(csptActorStates.getValue() != null && !csptActorStates.getValue().isEmpty()){
					String[] states = csptActorStates.getValue().split(",");
					for(String st : states){
						String[] sts = st.split(";");
						Reference ref = new Reference(sts[0], sts[1]);
						refs.add(ref);
					}
				}				
			}
			model.addAttribute("actorstates", refs);
			
			
			/**** Locale ****/
			domain.setLocale(locale);
			model.addAttribute("locale",locale);
			
			/**** Committee Names ****/
			HouseType houseType = houseTypes.get(0);
			this.populateCommitteeNames(model, houseType, locale);
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}					
			model.addAttribute("error", message);
		}
	}

	@Override
	protected void populateEdit(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
		try {
			/**** Locale ****/
			String locale=domain.getLocale();
			model.addAttribute("locale",locale);
			/**** House Types and Selected House Type ****/
			List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
			model.addAttribute("housetypes",houseTypes);
			String strHouseType=domain.getParameterValue("HOUSETYPE_"+locale).trim();
			if(!strHouseType.isEmpty()){
				model.addAttribute("selectedHouseType",strHouseType);
			}
			/**** Device Types ****/
			//List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
			List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class,"priority",ApplicationConstants.ASC, locale.toString());
				
			model.addAttribute("deviceTypes", deviceTypes);
			model.addAttribute("selectedDeviceType",domain.getParameterValue("DEVICETYPE_"+locale).trim());
			/**** Ministries ****/
			//List<Ministry> ministries= Ministry.findAssignedMinistries(locale);
			/** HACK to allow previous users to view assigned ministries and departments and subdepartments****/
			List<Ministry> ministries= MemberMinister.findAssignedMinistries(domain.getActiveFrom(), domain.getActiveTo(), locale);
			model.addAttribute("ministries",ministries);		
			String strMinistry=domain.getParameterValue("MINISTRY_"+locale);
			if(strMinistry!=null){
				if(!strMinistry.isEmpty()){
					model.addAttribute("selectedMinistry",strMinistry);
					String[] ministriesList=strMinistry.split("##");
					/**** Departments ****/
					//List<Department> departments=MemberMinister.findAssignedDepartments(ministriesList, locale);
//					List<Department> departments=Department.findAll(Department.class, "name", ApplicationConstants.ASC, locale);
//					model.addAttribute("departments",departments);
//					String strDepartment=domain.getParameterValue("DEPARTMENT_"+locale);
//					if(strDepartment!=null){
//						if(!strDepartment.isEmpty()){
//							model.addAttribute("selectedDepartment",strDepartment);
//							/**** Sub Departments ****/
//							String[] departmentList=strDepartment.split("##");
//							//List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministriesList,departmentList, locale);
//							List<SubDepartment> subDepartments= SubDepartment.findAll(SubDepartment.class, "name", ApplicationConstants.ASC, locale);
//							model.addAttribute("subdepartments", subDepartments);
//							String strSubDepartment=domain.getParameterValue("SUBDEPARTMENT_"+locale);
//							model.addAttribute("selectedSubDepartment",strSubDepartment);
//						}
//					}
					List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministriesList,domain.getActiveFrom(),domain.getActiveTo(), locale);
					model.addAttribute("subdepartments", subDepartments);
					String strSubDepartment=domain.getParameterValue("SUBDEPARTMENT_"+locale);
					model.addAttribute("selectedSubDepartment",strSubDepartment);
				}
			}		
			/**** User Group Types ****/
			List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
			model.addAttribute("userGroupTypes",userGroupTypes);
			
			/****Allowed Groups ****/
			String allowedGroups = domain.
					getParameterValue(ApplicationConstants.GROUPSALLOWED_KEY + "_" + locale);
			Credential credential = domain.getCredential();
			User user  = User.findByUserName(credential.getUsername(), locale);
			model.addAttribute("fullName",user.findFullName());	
			model.addAttribute("userFirstName", user.getFirstName());
			model.addAttribute("userMiddleName", user.getMiddleName());
    		model.addAttribute("userLastName", user.getLastName());
			if(allowedGroups != null && !allowedGroups.isEmpty()){
				model.addAttribute("groupsAllowed", allowedGroups);
			}else{
				if(user != null){
					model.addAttribute("groupsAllowed", user.getGroupsAllowed());	
				}
			}
			
			/**** Committee Names ****/
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			if(houseType != null) {
				this.populateCommitteeNames(model, houseType, locale);
			}
			String strCommitteeNames = 
				domain.getParameterValue("COMMITTEENAME_"+locale).trim();
			model.addAttribute("selectedCommitteeName", strCommitteeNames);
			
			
			/*** To get the sates for an actor ***/
			CustomParameter csptActorStates = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ACTOR_STATES, "");
			List<Reference> refs = new ArrayList<Reference>();			
			if(csptActorStates != null){
				if(csptActorStates.getValue() != null && !csptActorStates.getValue().isEmpty()){
					String[] states = csptActorStates.getValue().split(",");
					for(String st : states){
						String[] sts = st.split(";");
						Reference ref = new Reference(sts[0], sts[1]);
						refs.add(ref);
					}
				}				
			}
			model.addAttribute("actorstates", refs);
			model.addAttribute("selectedActorState", domain.getParameterValue("ACTORSTATE_" + locale));
			
			model.addAttribute("actorRemark", domain.getParameterValue("ACTORREMARK_" + locale));
			
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}					
			model.addAttribute("error", message);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
		try {
			/*
			 * Here we are collecting all the request parameters that begins with 'param_'
			 * and storing them as key/value pair.key is obtained by splitting request
			 * parameters that begins with param_ and storing index[1]
			 */
			Map<String,String[]> params=request.getParameterMap();
			Map<String,String> deviceTypeParams=new HashMap<String, String>();
			for(Entry<String,String[]> i:params.entrySet()){
				String key=i.getKey();
				if(key.startsWith("param_")){
					String[] values=params.get(key);
					if(values.length==1){
						deviceTypeParams.put(key.split("param_")[1],values[0]);
					}else{
						StringBuffer buffer=new StringBuffer();
						for(String j:values){
							buffer.append(j+"##");
						}
						deviceTypeParams.put(key.split("param_")[1],buffer.toString());
					}
				}
			}
			domain.setParameters(deviceTypeParams);			
			/** Edited By **/
			Object supportUserName = request.getSession().getAttribute("supportUserName");
			if(supportUserName!=null) {
				domain.setEditedBy(supportUserName.toString());			
			} else {
				domain.setEditedBy(this.getCurrentUser().getActualUsername());
			}		
			/** Edited As **/
			String strUserGroupType = request.getParameter("usergroupType");
			if(strUserGroupType != null && !strUserGroupType.isEmpty()){
				UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType, domain.getLocale());
				if(userGroupType!=null) {
					domain.setEditedAs(userGroupType.getName());
				}
			} else { //default user is administrator with role 'SUPER_ADMIN'
				Role role = Role.findByType(ApplicationConstants.ROLE_SUPER_ADMIN, domain.getLocale());
				if(role!=null) {
					domain.setEditedAs(role.getLocalizedName());
				}
			}
			/** Edited ON **/
			domain.setEditedOn(new Date());
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}					
			model.addAttribute("error", message);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
		try {
			/*
			 * Here we are collecting all the request parameters that begins with 'param_'
			 * and storing them as key/value pair.key is obtained by splitting request
			 * parameters that begins with param_ and storing index[1]
			 */
			Map<String,String[]> params=request.getParameterMap();
			Map<String,String> deviceTypeParams=new HashMap<String, String>();
			for(Entry<String,String[]> i:params.entrySet()){
				String key=i.getKey();
				if(key.startsWith("param_")){
					String[] values=params.get(key);
					if(values.length==1){
						deviceTypeParams.put(key.split("param_")[1],values[0]);
					}else{
						StringBuffer buffer=new StringBuffer();
						for(String j:values){
							buffer.append(j+"##");
						}
						deviceTypeParams.put(key.split("param_")[1],buffer.toString());
					}
				}
			}
			domain.setParameters(deviceTypeParams);
			/** Edited By **/
			Object supportUserName = request.getSession().getAttribute("supportUserName");
			if(supportUserName!=null) {
				domain.setEditedBy(supportUserName.toString());			
			} else {
				domain.setEditedBy(this.getCurrentUser().getActualUsername());
			}		
			/** Edited As **/
			String strUserGroupType = request.getParameter("usergroupType");
			if(strUserGroupType != null && !strUserGroupType.isEmpty()){
				UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType, domain.getLocale());
				if(userGroupType!=null) {
					domain.setEditedAs(userGroupType.getName());
				}
			} else { //default user is administrator with role 'SUPER_ADMIN'
				Role role = Role.findByType(ApplicationConstants.ROLE_SUPER_ADMIN, domain.getLocale());
				if(role!=null) {
					domain.setEditedAs(role.getLocalizedName());
				}
			}
			/** Edited ON **/
			domain.setEditedOn(new Date());
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}					
			model.addAttribute("error", message);
		}
	}

	@Override
	protected void customValidateCreate(final UserGroup domain, final BindingResult result,
			final HttpServletRequest request) {
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}

	@Override
	protected void customValidateUpdate(final UserGroup domain, final BindingResult result,
			final HttpServletRequest request) {
		// Check for version mismatch
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
	}
	
	private void populateCommitteeNames(final ModelMap model, 
			final HouseType houseType,
			final String locale) {
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		
		String houseTypeType = houseType.getType();
		HouseType bothHouse = HouseType.findByType(
				ApplicationConstants.BOTH_HOUSE, locale.toString());
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			List<CommitteeName> lowerHouseCommittees = 
				CommitteeName.find(houseType, locale.toString());
			committeeNames.addAll(lowerHouseCommittees);
			
			List<CommitteeName> bothHouseCommittees = 
				CommitteeName.find(bothHouse, locale.toString());
			committeeNames.addAll(bothHouseCommittees);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			List<CommitteeName> upperHouseCommittees = 
				CommitteeName.find(houseType, locale.toString());
			committeeNames.addAll(upperHouseCommittees);
			
			List<CommitteeName> bothHouseCommittees = 
				CommitteeName.find(bothHouse, locale.toString());
			committeeNames.addAll(bothHouseCommittees);
		}
		else if(houseTypeType.equals(ApplicationConstants.BOTH_HOUSE)) {
			List<CommitteeName> allCommittees =
				CommitteeName.findAll(locale.toString());
			committeeNames.addAll(allCommittees);
		}
		
		model.addAttribute("committeeNames", committeeNames);
	}

}
