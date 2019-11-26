/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberPersonalController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Degree;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.Gender;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MaritalStatus;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Nationality;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Relation;
import org.mkcl.els.domain.Religion;
import org.mkcl.els.domain.Reservation;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class MemberPersonalController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/member/personal")
public class MemberPersonalController extends GenericController<Member> {
	
	@Autowired 
	private ISecurityService securityService;

	//adding housetype
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateModule(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest, java.lang.String, org.mkcl.els.common.vo.AuthUser)
	 */
	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		model.addAttribute("housetype", request.getParameter("houseType"));
	}

	//init binders
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customInitBinderSuperClass(java.lang.Class, org.springframework.web.bind.WebDataBinder)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected <E extends BaseDomain> void customInitBinderSuperClass(
			final Class clazz, final WebDataBinder binder) {
		binder.registerCustomEditor(Gender.class, new BaseEditor(new Gender()));
		binder.registerCustomEditor(MaritalStatus.class, new BaseEditor(
				new MaritalStatus()));
		binder.registerCustomEditor(List.class, "professions",
				new CustomCollectionEditor(List.class) {

			@Override
			protected Object convertElement(final Object element) {
				String id = null;

				if (element instanceof String) {
					id = (String) element;
				}

				return id != null ? BaseDomain.findById(
						Profession.class, Long.valueOf(id)) : null;
			}
		});
		binder.registerCustomEditor(Nationality.class, new BaseEditor(
				new Nationality()));
		binder.registerCustomEditor(Address.class,
				new BaseEditor(new Address()));
		binder.registerCustomEditor(Contact.class,
				new BaseEditor(new Contact()));
		binder.registerCustomEditor(Title.class, new BaseEditor(new Title()));
	}
	//logic for populating domain and model during new and edit request
	/**
	 * Populate.
	 *
	 * @param model the model
	 * @param domain the domain
	 * @param request the request
	 */
	private void populate(final ModelMap model, final Member domain,
			final HttpServletRequest request){
		String locale=domain.getLocale();
		model.addAttribute("titles", Title.findAll(Title.class, "name",
				ApplicationConstants.ASC, locale));
		model.addAttribute("nationalities", Nationality.findAll(
				Nationality.class, "name", ApplicationConstants.ASC,
				locale));
		model.addAttribute("genders", Gender.findAll(Gender.class, "name",
				ApplicationConstants.ASC, locale));
		model.addAttribute("religions", Religion.findAll(Religion.class,
				"name", ApplicationConstants.DESC, locale));
		model.addAttribute("reservations", Reservation.findAll(
				Reservation.class, "name", ApplicationConstants.DESC,
				locale));
		model.addAttribute("relations", Relation.findAll(Relation.class,
				"name", ApplicationConstants.ASC, locale));
		model.addAttribute("degrees", Degree.findAll(Degree.class, "name",
				ApplicationConstants.ASC, locale));
		try {
			model.addAttribute("languages", Language.findAllSortedByPriorityAndName(locale));
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
		model.addAttribute("professions", Profession.findAll(Profession.class,"name", ApplicationConstants.ASC, locale));
		model.addAttribute("maritalStatuses", MaritalStatus.findAll(MaritalStatus.class, "name", ApplicationConstants.DESC,locale));
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateNew(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateNew(final ModelMap model, final Member domain,
			final String locale, final HttpServletRequest request) {
		domain.setLocale(locale.toString());
		populate(model, domain,request);
		model.addAttribute("familyCount",0);
		model.addAttribute("qualificationCount",0);
		//alias will always be enabled.
		domain.setAliasEnabled(true);
		//initially nof of sons,daughters and children is set to 0
		model.addAttribute("daughters",0);
		model.addAttribute("sons",0);
		model.addAttribute("children",0);
		//will be used to create default role
		model.addAttribute("house",request.getParameter("house"));
		//will be sued to load appropriate background image
		model.addAttribute("houseType",request.getParameter("houseType"));
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateEdit(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateEdit(final ModelMap model, final Member domain,
			final HttpServletRequest request) {
		populate(model, domain,request);
		model.addAttribute("familyMembers", domain.getFamilyMembers());
		model.addAttribute("familyCount", domain.getFamilyMembers().size());
		model.addAttribute("qualifications", domain.getQualifications());
		model.addAttribute("oldBirthDate", domain.getBirthDate());
		model.addAttribute("qualificationCount", domain.getQualifications()
				.size());
		int noOfDaughters=0;
		int noOfSons=0;
		int noOfChildren=0;
		String spouseName=null;
		if(!domain.getFamilyMembers().isEmpty()){
			for(FamilyMember i:domain.getFamilyMembers()){
				String relationType=i.getRelation().getType();
				if(relationType.equals(ApplicationConstants.DAUGHTER)){
					noOfDaughters++;
				}else if(relationType.equals(ApplicationConstants.SON)){
					noOfSons++;
				}else if(relationType.equals(ApplicationConstants.HUSBAND)||relationType.equals(ApplicationConstants.WIFE)){
					spouseName=i.getName();
					model.addAttribute("spouseName",spouseName);
				}
			}
		}
		noOfChildren=noOfSons+noOfDaughters;
		model.addAttribute("daughters",noOfDaughters);
		model.addAttribute("sons",noOfSons);
		model.addAttribute("children",noOfChildren);
		//will be used to create default role
		model.addAttribute("house",request.getParameter("house"));
		//will be sued to load appropriate background image
		//this is set in session in case of post and put to display the image
		if(request.getSession().getAttribute("houseType")==null){
			model.addAttribute("houseType",request.getParameter("houseType"));
		}else{
			model.addAttribute("houseType",request.getSession().getAttribute("houseType"));
			request.getSession().removeAttribute("houseType");
		}
		domain.setAliasEnabled(true);
		try {
			User existingMemberUser = User.findByNameBirthDate(domain.getFirstName(), domain.getMiddleName(), domain.getLastName(), domain.getBirthDate(), domain.getLocale());
			if(existingMemberUser!=null) {
				Credential credential = existingMemberUser.getCredential();
				if(credential!=null) {
					model.addAttribute("credentialEnabled", credential.isEnabled());
				}
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//private utility method for populating domain with family and qualifications
	/**
	 * Populate family qualification.
	 *
	 * @param domain the domain
	 * @param request the request
	 * @param result the result
	 */
	private void populateFamilyQualification(final Member domain, final HttpServletRequest request,final BindingResult result){
		List<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
		Integer familyCount = Integer.parseInt(request
				.getParameter("familyCount"));
		for (int i = 1; i <= familyCount; i++) {
			String relation=request.getParameter("familyMemberRelation"+ i);
			if(relation!=null){
				FamilyMember familyMember=new FamilyMember();

				String name=request.getParameter("familyMemberName"+i);
				if(name!=null){
					if(!name.isEmpty()){
						familyMember.setName(request.getParameter("familyMemberName"+ i));
					}//else{
					//result.rejectValue("familyMembers","NotEmpty");
					//}
				}

				if(!relation.isEmpty()){
					familyMember.setRelation((Relation) Relation.findById(Relation.class,Long.parseLong(relation)));
				}

				String id=request.getParameter("familyMemberId"+ i);
				if(id!=null){
					if(!id.isEmpty()){
						familyMember.setId(Long.parseLong(id));
					}
				}

				String version=request.getParameter("familyMemberVersion"+ i);
				if(version!=null){
					if(!version.isEmpty()){
						familyMember.setVersion(Long.parseLong(version));
					}
				}

				String locale=request.getParameter("familyMemberLocale"+ i);
				if(locale!=null){
					if(!locale.isEmpty()){
						familyMember.setLocale(locale);
					}
				}
				familyMembers.add(familyMember);
			}
		}
		domain.setFamilyMembers(familyMembers);

		List<Qualification> qualifications = new ArrayList<Qualification>();
		Integer qualificationCount = Integer.parseInt(request
				.getParameter("qualificationCount"));
		for (int i = 1; i <= qualificationCount; i++) {
			String degree=request.getParameter("qualificationDegree"+ i);
			if(degree!=null){
				Qualification qualification = new Qualification();

				String detail=request.getParameter("qualificationDetail" + i);
				if(detail!=null){
					qualification.setDetails(detail);
				}

				if(!degree.isEmpty()){
					qualification.setDegree((Degree) Degree.findById(Degree.class,Long.parseLong(degree)));
				}

				qualification.setLocale(domain.getLocale());

				String id=request.getParameter("qualificationId"+ i);
				if(id!=null){
					if(!id.isEmpty()){
						qualification.setId(Long.parseLong(id));
					}
				}

				String version=request.getParameter("qualificationVersion"+ i);
				if(version!=null){
					if(!version.isEmpty()){
						qualification.setVersion(Long.parseLong(version));
					}
				}
				String locale=request.getParameter("qualificationLocale"+ i);
				if(locale!=null){
					if(!locale.isEmpty()){
						qualification.setLocale(locale);
					}
				}
				qualifications.add(qualification);
			}
		}
		domain.setQualifications(qualifications);
	}
	//as we enter post and put we will populate domain with family members and qualifications
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateCreate(final Member domain,
			final BindingResult result, final HttpServletRequest request) {
		populateFamilyQualification(domain,request,result);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#preValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void preValidateUpdate(final Member domain,
			final BindingResult result, final HttpServletRequest request) {
		populateFamilyQualification(domain,request,result);
	}
	//in case of errors we need to re populate domain with populate edit logic
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateCreateIfErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateCreateIfErrors(final ModelMap model,
			final Member domain,
			final HttpServletRequest request) {
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#populateUpdateIfErrors(org.springframework.ui.ModelMap, org.mkcl.els.domain.BaseDomain, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void populateUpdateIfErrors(final ModelMap model, final Member domain,
			final HttpServletRequest request) {
		populateEdit(model, domain, request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "update_failed");
	}
	
	
	//here we are just checking for version mis match in validation.there is no check for duplicate entries
	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateCreate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateCreate(final Member domain,
			final BindingResult result, final HttpServletRequest request) {
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if(domain.getTitle()==null){
			result.rejectValue("title", "TitleEmpty");
		}
		if(domain.getFirstName()==null){
			result.rejectValue("firstName", "FirstNameEmpty");
		}
		if(domain.getFirstName().isEmpty()){
			result.rejectValue("firstName", "FirstNameEmpty");
		}       
		if(domain.getLastName()==null){
			result.rejectValue("lastName", "LastNameEmpty");
		}
		if(domain.getLastName().isEmpty()){
			result.rejectValue("lastName", "LastNameEmpty");
		} 
		if(domain.getBirthDate()==null){
			result.rejectValue("birthDate", "BirthDateEmpty");
		}
//		if(domain.getFirstNameEnglish()==null){
//			result.rejectValue("firstNameEnglish", "FirstNameEnglishEmpty");
//		}
//		if(domain.getFirstNameEnglish().isEmpty()){
//			result.rejectValue("firstNameEnglish", "FirstNameEnglishEmpty");
//		}       
//		if(domain.getLastNameEnglish()==null){
//			result.rejectValue("lastNameEnglish", "LastNameEnglishEmpty");
//		}
//		if(domain.getLastNameEnglish().isEmpty()){
//			result.rejectValue("lastNameEnglish", "LastNameEnglishEmpty");
//		}
		validateMember(domain, result);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.controller.GenericController#customValidateUpdate(org.mkcl.els.domain.BaseDomain, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected void customValidateUpdate(final Member domain,
			final BindingResult result, final HttpServletRequest request) {
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if(domain.getTitle()==null){
			result.rejectValue("title", "TitleEmpty");
		}
		if(domain.getFirstName()==null){
			result.rejectValue("firstName", "FirstNameEmpty");
		}
		if(domain.getFirstName().isEmpty()){
			result.rejectValue("firstName", "FirstNameEmpty");
		}       
		if(domain.getLastName()==null){
			result.rejectValue("lastName", "LastNameEmpty");
		}
		if(domain.getLastName().isEmpty()){
			result.rejectValue("lastName", "LastNameEmpty");
		} 
		if(domain.getBirthDate()==null){
			result.rejectValue("birthDate", "BirthDateEmpty");
		}
		validateMember(domain, result);
	}

	/**
	 * Delete family.
	 *
	 * @param id the id
	 * @param model the model
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/family/{id}/delete", method = RequestMethod.DELETE)
	public String deleteFamily(final @PathVariable("id") Long id,
			final ModelMap model, final HttpServletRequest request) {
		FamilyMember familyMember=FamilyMember.findById(FamilyMember.class, id);
		familyMember.remove();
		return "info";
	}

	/**
	 * Delete qualification.
	 *
	 * @param id the id
	 * @param model the model
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "/qualification/{id}/delete", method = RequestMethod.DELETE)
	public String deleteQualification(final @PathVariable("id") Long id,
			final ModelMap model, final HttpServletRequest request) {
		Qualification qualification=Qualification.findById(Qualification.class, id);
		qualification.remove();
		return "info";
	}

	@Override
	protected void populateAfterCreate(final ModelMap model,
			final Member domain, final HttpServletRequest request) {
		//for displaying image on edit page after submission
		String houseTypeId = request.getParameter("houseType");
		request.getSession().setAttribute("houseType", houseTypeId);
		//here when a new record is created an entry will be made in house member role asspciation
		//with default role.This is done so that a new record always belong to some house on creation.
		String isMember=request.getParameter("isMember");
		if(isMember!=null){
			if(!isMember.isEmpty()){
				if(isMember.equals("true")){
					Long houseId=Long.parseLong(request.getParameter("house"));
					House house=House.findById(House.class,houseId);
					HouseType houseType=null;
					if(house!=null){
						HouseMemberRoleAssociation houseMemberRoleAssociation = new HouseMemberRoleAssociation();
						houseType=house.getType();
						if(houseType!=null && houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							Calendar cal = Calendar.getInstance();		
							cal.setTime(new Date());
							houseMemberRoleAssociation.setFromDate(cal.getTime());							
							CustomParameter csptDefaultMemberTenureCouncil = CustomParameter.findByName(CustomParameter.class, "DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE", "");
							if(csptDefaultMemberTenureCouncil!=null 
									&& csptDefaultMemberTenureCouncil.getValue()!=null
									&& !csptDefaultMemberTenureCouncil.getValue().isEmpty()) {
								cal.add(Calendar.YEAR, Integer.parseInt(csptDefaultMemberTenureCouncil.getValue()));		
								System.out.println(cal.getTime());
								houseMemberRoleAssociation.setToDate(cal.getTime());
							} else {
								cal.add(Calendar.YEAR, Integer.parseInt(ApplicationConstants.DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE));
								System.out.println(cal.getTime());
								houseMemberRoleAssociation.setToDate(cal.getTime());
							}							
						}else if(houseType!=null&&houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							houseMemberRoleAssociation.setFromDate(house.getFirstDate());
							houseMemberRoleAssociation.setToDate(house.getLastDate());
						}
						houseMemberRoleAssociation.setHouse(house);
						Date currentDate=new Date();
						if(house.getLastDate()!=null){
							if(house.getLastDate().after(currentDate)){
								houseMemberRoleAssociation.setIsSitting(true);
							}
						}
						houseMemberRoleAssociation.setMember(domain);
						houseMemberRoleAssociation.setLocale(domain.getLocale());
						houseMemberRoleAssociation.setRecordIndex(1);
						CustomParameter roleLocalized=CustomParameter.findByName(CustomParameter.class,"DEFAULT_ROLE","");
						MemberRole memberRole = MemberRole.findByNameHouseTypeLocale(roleLocalized.getValue(), house.getType().getId(), domain.getLocale());
						houseMemberRoleAssociation.setRole(memberRole);
						houseMemberRoleAssociation.persist();
						
						/** creation of corresponding user for the member if not created already **/
						try {
							User existingMemberUser = User.findByNameBirthDate(domain.getFirstName(), domain.getMiddleName(), domain.getLastName(), domain.getBirthDate(), domain.getLocale());
							if(existingMemberUser==null || existingMemberUser.getId()==null) { //user entry is needed to be created
								User user = new User();
								if(domain.getTitle()!=null) {
									user.setTitle(domain.getTitle().getName());
								} else {
									user.setTitle("");
								}
								user.setFirstName(domain.getFirstName());
								user.setMiddleName(domain.getMiddleName());
								user.setLastName(domain.getLastName());
								user.setBirthDate(domain.getBirthDate());								
								user.setBirthPlace(domain.getBirthPlace());								
								user.setHouseType(houseType);
								user.setJoiningDate(new Date());
								//set credential for the user
								Credential credential = new Credential();
								StringBuffer usernameBuffer = new StringBuffer("");
								if(domain.getTitle()!=null && domain.getTitle().getType()!=null
										&& !domain.getTitle().getType().isEmpty()) {
									CustomParameter csptTitlesAllowedForUsernameCreation = CustomParameter.findByName(CustomParameter.class, "TITLES_ALLOWED_FOR_USERNAME_CREATION", "");
									if(csptTitlesAllowedForUsernameCreation!=null 
											&& csptTitlesAllowedForUsernameCreation.getValue()!=null) {
										for(String title: csptTitlesAllowedForUsernameCreation.getValue().split(",")) {
											title = title.trim();
											if(domain.getTitle().getType().trim().equals(title)) {
												usernameBuffer.append(domain.getTitle().getType());
												break;
											}
										}
										if(usernameBuffer.toString().isEmpty()) {
											if(domain.getGender()!=null && domain.getGender().getType()!=null) {
												if(domain.getGender().getType().equals("male")) {
													usernameBuffer.append("shri");
												} else if(domain.getGender().getType().equals("female")) {
													usernameBuffer.append("smt");
												}												
											}
										}
									} else {										
										usernameBuffer.append(domain.getTitle().getType());
									}
								}
								if(domain.getFirstNameEnglish()!=null && !domain.getFirstNameEnglish().isEmpty()) {
									usernameBuffer.append(domain.getFirstNameEnglish().toLowerCase());
								}
								//find if multiple members are having same first name as well as same last name
								Map<String, String> memberNameParameters = new HashMap<String, String>();
								memberNameParameters.put("firstName", domain.getFirstName());
								memberNameParameters.put("lastName", domain.getLastName());
								List<Member> membersWithSameFirstNameLastName = Member.findAllByFieldNames(Member.class, memberNameParameters, "lastName", ApplicationConstants.ASC, domain.getLocale());
								if(membersWithSameFirstNameLastName.size()>1) {
									if(domain.getMiddleNameEnglish()!=null && !domain.getMiddleNameEnglish().isEmpty()) {
										usernameBuffer.append(domain.getMiddleNameEnglish().toLowerCase().charAt(0));
									}
								}								
								if(domain.getLastNameEnglish()!=null && !domain.getLastNameEnglish().isEmpty()) {
									usernameBuffer.append(domain.getLastNameEnglish().toLowerCase());
								}
								credential.setUsername(usernameBuffer.toString());
								//generate and set random complex password for member login
								String strPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
								String encodedPassword = securityService.getEncodedPassword(strPassword);
								credential.setPassword(encodedPassword);
								usernameBuffer.append("@");
								usernameBuffer.append(ApplicationConstants.DEFAULT_EMAIL_HOSTNAME);
								credential.setEmail(usernameBuffer.toString());
								//assign default member role as per housetype
								Role memberUserRole = null;
								if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
									memberUserRole = Role.findByType(ApplicationConstants.MEMBER_LOWERHOUSE, domain.getLocale());
								} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
									memberUserRole = Role.findByType(ApplicationConstants.MEMBER_UPPERHOUSE, domain.getLocale());
								}
								Set<Role> roles = new LinkedHashSet<Role>();
								roles.add(memberUserRole);
								credential.setRoles(roles);
								String isEnabled = request.getParameter("isEnabled");
								if(isEnabled!=null && !isEnabled.isEmpty()) {
									credential.setEnabled(Boolean.parseBoolean(isEnabled));
								}								
								credential.setPasswordChangeCount(1);
								credential.setAllowedForMultiLogin(false);
								//credential.setLocale("");
								credential.persist();	
								user.setCredential(credential);
								user.setStartURL(ApplicationConstants.DEFAULT_MEMBER_USER_START_URL);
								user.setLocale(domain.getLocale());
								user.persist();
								/** creation of corresponding usergroup for the member **/
								UserGroup userGroup = new UserGroup();
								userGroup.setCredential(credential);
								UserGroupType userGroupType = UserGroupType.findByType(ApplicationConstants.MEMBER, domain.getLocale());
								userGroup.setUserGroupType(userGroupType);
								if(houseType!=null && houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Calendar cal = Calendar.getInstance();		
									cal.setTime(new Date());
									userGroup.setActiveFrom(cal.getTime());
									CustomParameter csptDefaultMemberTenureCouncil = CustomParameter.findByName(CustomParameter.class, "DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE", "");
									if(csptDefaultMemberTenureCouncil!=null 
											&& csptDefaultMemberTenureCouncil.getValue()!=null
											&& !csptDefaultMemberTenureCouncil.getValue().isEmpty()) {
										cal.add(Calendar.YEAR, Integer.parseInt(csptDefaultMemberTenureCouncil.getValue()));		
										userGroup.setActiveTo(cal.getTime());
									} else {
										cal.add(Calendar.YEAR, Integer.parseInt(ApplicationConstants.DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE));
										userGroup.setActiveTo(cal.getTime());
									}							
								}else if(houseType!=null&&houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									userGroup.setActiveFrom(new Date());
									userGroup.setActiveTo(house.getLastDate());
								}
								//default usergroup parameters
								Map<String,String> userGroupParams=new HashMap<String, String>();
								userGroupParams.put(ApplicationConstants.HOUSETYPE_KEY+"_"+domain.getLocale(), houseType.getName());
								userGroupParams.put(ApplicationConstants.ACTORSTATE_KEY+"_"+domain.getLocale(), ApplicationConstants.ACTOR_ACTIVE);
								userGroupParams.put(ApplicationConstants.ACTORREMARK_KEY+"_"+domain.getLocale(), "");
								userGroupParams.put(ApplicationConstants.GROUPSALLOWED_KEY+"_"+domain.getLocale(), "");
								List<DeviceType> deviceTypes = null;
								CustomParameter csptAllowedDeviceTypesForMember = CustomParameter.findByName(CustomParameter.class, "ALLOWED_DEVICETYPES_FOR_MEMBER", domain.getLocale());
								if(csptAllowedDeviceTypesForMember!=null 
										&& csptAllowedDeviceTypesForMember.getValue()!=null
										&& !csptAllowedDeviceTypesForMember.getValue().isEmpty()) {
//									deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, domain.getLocale());
//									if(deviceTypes!=null && !deviceTypes.isEmpty()) {
//										if(deviceTypes.size()==1) {
//											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), deviceTypes.get(0).getName());
//										} else {
//											StringBuffer buffer=new StringBuffer();
//											for(DeviceType j:deviceTypes){
//												buffer.append(j.getName()+"##");
//											}
//											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), buffer.toString());
//										}
//									}
									userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), csptAllowedDeviceTypesForMember.getValue());
								} else {
									deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, domain.getLocale());
									if(deviceTypes!=null && !deviceTypes.isEmpty()) {
										if(deviceTypes.size()==1) {
											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), deviceTypes.get(0).getName());
										} else {
											StringBuffer buffer=new StringBuffer();
											for(DeviceType j:deviceTypes){
												buffer.append(j.getName()+"##");
											}
											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), buffer.toString());
										}
									}
								}								
								List<Ministry> ministries=Ministry.findAssignedMinistries(domain.getLocale());
								if(ministries!=null && !ministries.isEmpty()) {
									if(ministries.size()==1) {
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), ministries.get(0).getName());
									} else {
										StringBuffer buffer=new StringBuffer();
										for(Ministry j:ministries){
											buffer.append(j.getName()+"##");
										}
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), buffer.toString());
									}
								} else {
									CustomParameter csptCurrentMinistryParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_MINISTRIES_FOR_USERGROUP_CREATION", domain.getLocale());
									if(csptCurrentMinistryParamValue!=null && csptCurrentMinistryParamValue.getValue()!=null) {
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), csptCurrentMinistryParamValue.getValue());
									}
								}
								String strMinistry=userGroupParams.get(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale());
								if(strMinistry!=null){
									if(!strMinistry.isEmpty()){
										String[] ministriesList=strMinistry.split("##");
										List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministriesList, domain.getLocale());
										if(subDepartments!=null && !subDepartments.isEmpty()) {
											if(subDepartments.size()==1) {
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), subDepartments.get(0).getName());
											} else {
												StringBuffer buffer=new StringBuffer();
												for(SubDepartment j:subDepartments){
													buffer.append(j.getName()+"##");
												}
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), buffer.toString());
											}
										} else {
											CustomParameter csptCurrentDepartmentParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_SUBDEPARTMENTS_FOR_USERGROUP_CREATION", domain.getLocale());
											if(csptCurrentDepartmentParamValue!=null && csptCurrentDepartmentParamValue.getValue()!=null) {
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), csptCurrentDepartmentParamValue.getValue());
											}
										}
									}
								}
								userGroup.setParameters(userGroupParams);
								/** Edited By **/
								Object supportUserName = request.getSession().getAttribute("supportUserName");
								if(supportUserName!=null) {
									userGroup.setEditedBy(supportUserName.toString());			
								} else {
									userGroup.setEditedBy(this.getCurrentUser().getActualUsername());
								}		
								/** Edited As **/
								Role role = Role.findByType(ApplicationConstants.ROLE_SUPER_ADMIN, domain.getLocale()); //default user is administrator with role 'SUPER_ADMIN'
								if(role!=null) {
									userGroup.setEditedAs(role.getLocalizedName());
								}
								/** Edited ON **/
								userGroup.setEditedOn(new Date());
								userGroup.setLocale(domain.getLocale());
								userGroup.persist();								
							}
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}				
				}
			}
		}
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final Member domain,
			final HttpServletRequest request) {
		//for displaying image on edit page after submission
		request.getSession().setAttribute("houseType",request.getParameter("houseType"));
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
			final Member domain, final HttpServletRequest request) {
		populateIfNoErrors(model, domain, request);
		try {
			Member member = Member.findById(Member.class, domain.getId());
			User existingMemberUser = User.findbyNameBirthDate(member.getFirstName(), member.getMiddleName(), member.getLastName(), member.getBirthDate());
			if(existingMemberUser!=null && existingMemberUser.getId()!=null)
			{		
				//update birthdate in user entry of member
				existingMemberUser.setBirthDate(domain.getBirthDate());
				//update localized names in user entry of member
				existingMemberUser.setFirstName(domain.getFirstName());
				existingMemberUser.setMiddleName(domain.getMiddleName());
				existingMemberUser.setLastName(domain.getLastName());				
				Credential credential = existingMemberUser.getCredential();				
				if(!credential.isEnabled()) {					
					//enable credential once naming is finalized
					String isEnabled = request.getParameter("isEnabled");
					if(isEnabled!=null && !isEnabled.isEmpty()) {
						if(Boolean.parseBoolean(isEnabled)) {
							credential.setEnabled(true);
							//update username in credential of the user entry (in case of corrections in english name of member)
							if(!member.getFirstNameEnglish().equals(domain.getFirstNameEnglish())
									|| !member.getMiddleNameEnglish().equals(domain.getMiddleNameEnglish())
									|| !member.getLastNameEnglish().equals(domain.getLastNameEnglish())) {						
								StringBuffer usernameBuffer = new StringBuffer("");
								if(domain.getTitle()!=null && domain.getTitle().getType()!=null
										&& !domain.getTitle().getType().isEmpty()) {
									CustomParameter csptTitlesAllowedForUsernameCreation = CustomParameter.findByName(CustomParameter.class, "TITLES_ALLOWED_FOR_USERNAME_CREATION", "");
									if(csptTitlesAllowedForUsernameCreation!=null 
											&& csptTitlesAllowedForUsernameCreation.getValue()!=null) {
										for(String title: csptTitlesAllowedForUsernameCreation.getValue().split(",")) {
											title = title.trim();
											if(domain.getTitle().getType().trim().equals(title)) {
												usernameBuffer.append(domain.getTitle().getType());
												break;
											}
										}
										if(usernameBuffer.toString().isEmpty()) {
											if(domain.getGender()!=null && domain.getGender().getType()!=null) {
												if(domain.getGender().getType().equals("male")) {
													usernameBuffer.append("shri");
												} else if(domain.getGender().getType().equals("female")) {
													usernameBuffer.append("smt");
												}												
											}
										}
									} else {										
										usernameBuffer.append(domain.getTitle().getType());
									}
								}
								if(domain.getFirstNameEnglish()!=null && !domain.getFirstNameEnglish().isEmpty()) {
									usernameBuffer.append(domain.getFirstNameEnglish().toLowerCase());
								}
								//find if multiple members are having same first name as well as same last name
								Map<String, String> memberNameParameters = new HashMap<String, String>();
								memberNameParameters.put("firstName", domain.getFirstName());
								memberNameParameters.put("lastName", domain.getLastName());
								List<Member> membersWithSameFirstNameLastName = Member.findAllByFieldNames(Member.class, memberNameParameters, domain.getLocale(), "lastName", ApplicationConstants.ASC);
								if(membersWithSameFirstNameLastName.size()>1) {
									if(domain.getMiddleNameEnglish()!=null && !domain.getMiddleNameEnglish().isEmpty()) {
										usernameBuffer.append(domain.getMiddleNameEnglish().toLowerCase().charAt(0));
									}
								}								
								if(domain.getLastNameEnglish()!=null && !domain.getLastNameEnglish().isEmpty()) {
									usernameBuffer.append(domain.getLastNameEnglish().toLowerCase());
								}
								credential.setUsername(usernameBuffer.toString());
								//update email id according to username updated above
								usernameBuffer.append("@");
								usernameBuffer.append(ApplicationConstants.DEFAULT_EMAIL_HOSTNAME);
								credential.setEmail(usernameBuffer.toString());
							}
							credential.merge();
						}
					}
				} else { //revert naming changes if any
//						domain.setFirstNameEnglish(member.getFirstNameEnglish());
//						domain.setMiddleNameEnglish(member.getMiddleNameEnglish());
//						domain.setLastNameEnglish(member.getLastNameEnglish());			
					
				}
				if(credential.isEnabled()) { //only for active members
					/** creation of corresponding usergroup for the member in case of new house formed **/
					CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
					if(csptNewHouseFormationInProcess!=null && csptNewHouseFormationInProcess.getValue()!=null
							&& csptNewHouseFormationInProcess.getValue().equals("YES")) {
						UserGroupType memberUGType = UserGroupType.findByType(ApplicationConstants.MEMBER, domain.getLocale());
						UserGroup existingUserGroup = UserGroup.findActive(credential, memberUGType, new Date(), domain.getLocale());
						if(existingUserGroup==null || existingUserGroup.getId()==null) {
							String currentHouseType = request.getParameter("houseType");
							System.out.println("currentHouseType: " + currentHouseType);
							House house = House.findById(House.class, Long.parseLong("2600"));// required house id to be parsed (mostly latest house id of required housetype)
							//Long houseId=Long.parseLong(request.getParameter("house"));
							//House house=House.findById(House.class,houseId);
							HouseType houseType=null;
							if(house!=null){
								houseType = house.getType();
								UserGroup userGroup = new UserGroup();
								userGroup.setCredential(credential);
								UserGroupType userGroupType = UserGroupType.findByType(ApplicationConstants.MEMBER, domain.getLocale());
								userGroup.setUserGroupType(userGroupType);
								if(houseType!=null && houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
									Calendar cal = Calendar.getInstance();		
									cal.setTime(new Date());
									userGroup.setActiveFrom(cal.getTime());
									CustomParameter csptDefaultMemberTenureCouncil = CustomParameter.findByName(CustomParameter.class, "DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE", "");
									if(csptDefaultMemberTenureCouncil!=null 
											&& csptDefaultMemberTenureCouncil.getValue()!=null
											&& !csptDefaultMemberTenureCouncil.getValue().isEmpty()) {
										cal.add(Calendar.YEAR, Integer.parseInt(csptDefaultMemberTenureCouncil.getValue()));		
										userGroup.setActiveTo(cal.getTime());
									} else {
										cal.add(Calendar.YEAR, Integer.parseInt(ApplicationConstants.DEFAULT_MEMBER_TENURE_YEARS_UPPERHOUSE));
										userGroup.setActiveTo(cal.getTime());
									}							
								}else if(houseType!=null&&houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
									userGroup.setActiveFrom(new Date());
									userGroup.setActiveTo(house.getLastDate());
								}
								//default usergroup parameters
								Map<String,String> userGroupParams=new HashMap<String, String>();
								userGroupParams.put(ApplicationConstants.HOUSETYPE_KEY+"_"+domain.getLocale(), houseType.getName());
								userGroupParams.put(ApplicationConstants.ACTORSTATE_KEY+"_"+domain.getLocale(), ApplicationConstants.ACTOR_ACTIVE);
								userGroupParams.put(ApplicationConstants.ACTORREMARK_KEY+"_"+domain.getLocale(), "");
								userGroupParams.put(ApplicationConstants.GROUPSALLOWED_KEY+"_"+domain.getLocale(), "");
								List<DeviceType> deviceTypes = null;
								CustomParameter csptAllowedDeviceTypesForMember = CustomParameter.findByName(CustomParameter.class, "ALLOWED_DEVICETYPES_FOR_MEMBER", domain.getLocale());
								if(csptAllowedDeviceTypesForMember!=null 
										&& csptAllowedDeviceTypesForMember.getValue()!=null
										&& !csptAllowedDeviceTypesForMember.getValue().isEmpty()) {
//										deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, domain.getLocale());
//										if(deviceTypes!=null && !deviceTypes.isEmpty()) {
//											if(deviceTypes.size()==1) {
//												userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), deviceTypes.get(0).getName());
//											} else {
//												StringBuffer buffer=new StringBuffer();
//												for(DeviceType j:deviceTypes){
//													buffer.append(j.getName()+"##");
//												}
//												userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), buffer.toString());
//											}
//										}
									userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), csptAllowedDeviceTypesForMember.getValue());
								} else {
									deviceTypes=DeviceType.findAll(DeviceType.class, "name",ApplicationConstants.ASC, domain.getLocale());
									if(deviceTypes!=null && !deviceTypes.isEmpty()) {
										if(deviceTypes.size()==1) {
											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), deviceTypes.get(0).getName());
										} else {
											StringBuffer buffer=new StringBuffer();
											for(DeviceType j:deviceTypes){
												buffer.append(j.getName()+"##");
											}
											userGroupParams.put(ApplicationConstants.DEVICETYPE_KEY+"_"+domain.getLocale(), buffer.toString());
										}
									}
								}								
								List<Ministry> ministries=Ministry.findAssignedMinistries(domain.getLocale());
								if(ministries!=null && !ministries.isEmpty()) {
									if(ministries.size()==1) {
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), ministries.get(0).getName());
									} else {
										StringBuffer buffer=new StringBuffer();
										for(Ministry j:ministries){
											buffer.append(j.getName()+"##");
										}
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), buffer.toString());
									}
								} else {
									CustomParameter csptCurrentMinistryParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_MINISTRIES_FOR_USERGROUP_CREATION", domain.getLocale());
									if(csptCurrentMinistryParamValue!=null && csptCurrentMinistryParamValue.getValue()!=null) {
										userGroupParams.put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), csptCurrentMinistryParamValue.getValue());
									}
								}
								String strMinistry=userGroupParams.get(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale());
								if(strMinistry!=null){
									if(!strMinistry.isEmpty()){
										String[] ministriesList=strMinistry.split("##");
										List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministriesList, domain.getLocale());
										if(subDepartments!=null && !subDepartments.isEmpty()) {
											if(subDepartments.size()==1) {
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), subDepartments.get(0).getName());
											} else {
												StringBuffer buffer=new StringBuffer();
												for(SubDepartment j:subDepartments){
													buffer.append(j.getName()+"##");
												}
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), buffer.toString());
											}
										} else {
											CustomParameter csptCurrentDepartmentParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_SUBDEPARTMENTS_FOR_USERGROUP_CREATION", domain.getLocale());
											if(csptCurrentDepartmentParamValue!=null && csptCurrentDepartmentParamValue.getValue()!=null) {
												userGroupParams.put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), csptCurrentDepartmentParamValue.getValue());
											}
										}
									}
								}
								userGroup.setParameters(userGroupParams);
								/** Edited By **/
								Object supportUserName = request.getSession().getAttribute("supportUserName");
								if(supportUserName!=null) {
									userGroup.setEditedBy(supportUserName.toString());			
								} else {
									userGroup.setEditedBy(this.getCurrentUser().getActualUsername());
								}		
								/** Edited As **/
								Role role = Role.findByType(ApplicationConstants.ROLE_SUPER_ADMIN, domain.getLocale()); //default user is administrator with role 'SUPER_ADMIN'
								if(role!=null) {
									userGroup.setEditedAs(role.getLocalizedName());
								}
								/** Edited ON **/
								userGroup.setEditedOn(new Date());
								userGroup.setLocale(domain.getLocale());
								userGroup.persist();
							}
						} else {							
							String ministryParamValue = existingUserGroup.getParameterValue(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale());
							if(ministryParamValue==null || ministryParamValue.isEmpty()) {
								CustomParameter csptCurrentMinistryParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_MINISTRIES_FOR_USERGROUP_CREATION", domain.getLocale());
								if(csptCurrentMinistryParamValue!=null && csptCurrentMinistryParamValue.getValue()!=null) {
									existingUserGroup.getParameters().put(ApplicationConstants.MINISTRY_KEY+"_"+domain.getLocale(), csptCurrentMinistryParamValue.getValue());
									existingUserGroup.merge();
								}
							}
							String departmentParamValue = existingUserGroup.getParameterValue(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale());
							if(departmentParamValue==null || departmentParamValue.isEmpty()) {
								CustomParameter csptCurrentDepartmentParamValue = CustomParameter.findByName(CustomParameter.class, "CURRENT_ALL_SUBDEPARTMENTS_FOR_USERGROUP_CREATION", domain.getLocale());
								if(csptCurrentDepartmentParamValue!=null && csptCurrentDepartmentParamValue.getValue()!=null) {
									existingUserGroup.getParameters().put(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+domain.getLocale(), csptCurrentDepartmentParamValue.getValue());
									existingUserGroup.merge();
								}
							}
						}
					}						
				}
			}			
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void validateMember(Member domain, BindingResult result) {
		// Validation for duplicate member entered
		Member duplicateMember = null;
		if (domain.getFirstName() != null 
				&& !domain.getFirstName().isEmpty()
				&& domain.getMiddleName() != null
				&& !domain.getMiddleName().isEmpty()
				&& domain.getLastName() != null
				&& !domain.getLastName().isEmpty()
				&& domain.getBirthDate() != null) {
			duplicateMember = Member.findDuplicateMember(domain.getId(), domain.getFirstName(), domain.getMiddleName(), domain.getLastName(),
					domain.getBirthDate(), domain.getLocale());
		} else if (domain.getFirstName() != null
				&& !domain.getFirstName().isEmpty()
				&& domain.getLastName() != null
				&& !domain.getLastName().isEmpty()
				&& domain.getBirthDate() != null) {
			duplicateMember = Member.findDuplicateMember(domain.getId(), domain.getFirstName(),
					domain.getLastName(), domain.getBirthDate(),
					domain.getLocale());
		}

		if (duplicateMember != null && duplicateMember.getId() != null) {
			result.rejectValue("version", "Member Already Exists.");
		}
	}
}
