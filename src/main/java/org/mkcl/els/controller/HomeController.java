/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.HomeController.java
 * Created On: Mar 30, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DocumentLink;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartySymbol;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class HomeController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
public class HomeController extends BaseController {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
    .getLogger(HomeController.class);

    /** The Constant DEFAULT_LOCALE. */
    private static final String DEFAULT_LOCALE = "mr_IN";

    /** The Constant ASC. */
    private static final String ASC = "asc";

    /**
     * Login.
     *
     * @param lang the lang
     * @param model the model
     * @param request the request
     * @param response the response
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) final String lang,
            final Model model, 
            final HttpServletRequest request,
            final HttpServletResponse response, 
            final Locale locale) {
        List<ApplicationLocale> supportedLocales = ApplicationLocale.findAll(
                ApplicationLocale.class, "language", ASC, "");
        if (lang != null) {
            model.addAttribute("selectedLocale", lang);
        }
        else {
            model.addAttribute("selectedLocale", DEFAULT_LOCALE);
        }
        CustomParameter cpSecretKey = CustomParameter.findByName(CustomParameter.class, "SECRET_KEY_FOR_ENCRYPTION", "");
        if(cpSecretKey != null){
        	model.addAttribute("secret_key", cpSecretKey.getValue());
        }
        CustomParameter cpEncryptionRequired = CustomParameter.findByName(CustomParameter.class, "PASSWORD_ENCRYTPTION_REQUIRED", "");
        if(cpEncryptionRequired != null){
        	model.addAttribute("passwordEncryptionReq", cpEncryptionRequired.getValue());
        }
        CustomParameter cpLoginDisabledNotificationFlag = CustomParameter.findByName(CustomParameter.class, "LOGIN_DISABLED_NOTIFICATION_FLAG", "");
        if(cpLoginDisabledNotificationFlag != null){
        	model.addAttribute("login_disabled_notification_flag", cpLoginDisabledNotificationFlag.getValue());
        	String loginDisabledNotificationFlag = cpLoginDisabledNotificationFlag.getValue();
        	if(loginDisabledNotificationFlag!=null) {
        		model.addAttribute("login_disabled_notification_flag", loginDisabledNotificationFlag);
        		if(loginDisabledNotificationFlag.equals("ON")) {
        			CustomParameter cpLoginDisabledNotificationUserGroupTypes = CustomParameter.findByName(CustomParameter.class, "LOGIN_DISABLED_NOTIFICATION_USERGROUP_TYPES", "");
        			if(cpLoginDisabledNotificationUserGroupTypes!=null
        					&& cpLoginDisabledNotificationUserGroupTypes.getValue()!=null) {
        				List<String> loginDisabledUsernames = new ArrayList<String>();
        				for(String ugt: cpLoginDisabledNotificationUserGroupTypes.getValue().split(",")) {
        					List<String> usernames = Credential.findAllActiveUsernamesByUserGroupType(ugt.trim(), new Date(), locale.toString());
        					if(usernames!=null && !usernames.isEmpty()) {
        						loginDisabledUsernames.addAll(usernames);
        					} 					
        				}
        				model.addAttribute("loginDisabledUsernames", loginDisabledUsernames);
        			}
        		}
        	} else {
        		model.addAttribute("login_disabled_notification_flag", "");
        	}
        }
        model.addAttribute("locales", supportedLocales);
        return "login";
    }

    /**
     * Home.
     *
     * @param model the model
     * @param request the request
     * @param locale the locale
     * @return the string
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        //here we will initialize authuser wih locale dependent data such as
        //firstname,middlename,lastname,title and housetype.
        //This housetype will be same as the housetype alloted to user while
        //creating user entry.
        AuthUser user=this.getCurrentUser();
        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");
        User authenticatedUser=User.findByFieldName(User.class,"credential",credential, locale.toString());
        this.getCurrentUser().setFirstName(authenticatedUser.getFirstName());
        this.getCurrentUser().setMiddleName(authenticatedUser.getMiddleName());
        this.getCurrentUser().setLastName(authenticatedUser.getLastName());
        this.getCurrentUser().setTitle(authenticatedUser.getTitle());
        this.getCurrentUser().setHouseType(authenticatedUser.getHouseType().getType());
        this.getCurrentUser().setUserId(authenticatedUser.getId());
        this.getCurrentUser().setBirthDate(authenticatedUser.getBirthDate());
        this.getCurrentUser().setGroupsAllowed(authenticatedUser.getGroupsAllowed());
        this.getCurrentUser().setStartURL(authenticatedUser.getStartURL());
        model.addAttribute("authusername", this.getCurrentUser().getUsername());
        model.addAttribute("authtitle", this.getCurrentUser().getTitle());
        model.addAttribute("authfirstname", this.getCurrentUser().getFirstName());
        model.addAttribute("authmiddlename", this.getCurrentUser().getMiddleName());
        model.addAttribute("authlastname", this.getCurrentUser().getLastName());
        //setting date and time formats to be used.
        String dateFormat= ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_DATEFORMAT", "")).getValue();
        model.addAttribute("dateFormat",dateFormat);
        String timeFormat=((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_TIMEFORMAT", "")).getValue();
        model.addAttribute("timeFormat",timeFormat);
        //right now all menus are visible to all.
        Set<Role> roles=this.getCurrentUser().getRoles();
        StringBuffer buffer=new StringBuffer();
        Boolean isUserAllowedForDashboardView = false; //for showing dashboard post login to allowed users only
        CustomParameter rolesAllowedForDashBoardViewCP = CustomParameter.findByName(CustomParameter.class, "ROLES_ALLOWED_FOR_DASHBOARD_VIEW", "");
        for(Role i:roles){
        	if(rolesAllowedForDashBoardViewCP!=null 
        			&& rolesAllowedForDashBoardViewCP.getValue()!=null
        			&& !rolesAllowedForDashBoardViewCP.getValue().isEmpty()) {
        		for(String allowedRole: rolesAllowedForDashBoardViewCP.getValue().split(",")) {
        			if(i.getType().trim().equalsIgnoreCase(allowedRole.trim())) {
        				isUserAllowedForDashboardView = true;
        				break;
        			}
        		}
        	}
        	if(i.getMenusAllowed()!=null && !i.getMenusAllowed().isEmpty()){
        		String menuAllowedIds = i.getMenusAllowed().replaceAll("##",",");
        		StringBuffer filteredBuffer=new StringBuffer();
        		CustomParameter rolesWithHouseTypeBasedDeviceTypeMenusCP = CustomParameter.findByName(CustomParameter.class, "ROLES_WITH_HOUSETYPE_BASED_DEVICETYPEMENUS_CP", "");
    			if(rolesWithHouseTypeBasedDeviceTypeMenusCP!=null && rolesWithHouseTypeBasedDeviceTypeMenusCP.getValue()!=null
    					&& !rolesWithHouseTypeBasedDeviceTypeMenusCP.getValue().isEmpty()) {
    				boolean isRoleWithHouseTypeBasedDeviceTypeMenus = false;
    				for(String roleWithHouseTypeBasedDeviceTypeMenus: rolesWithHouseTypeBasedDeviceTypeMenusCP.getValue().split(",")) {
    					roleWithHouseTypeBasedDeviceTypeMenus = roleWithHouseTypeBasedDeviceTypeMenus.trim();
    					if(roleWithHouseTypeBasedDeviceTypeMenus.equals(i.getType().trim())) {
    						isRoleWithHouseTypeBasedDeviceTypeMenus = true;
    						break;
    					}
    				}
    				if(isRoleWithHouseTypeBasedDeviceTypeMenus) {
    					for(String menuAllowedId: menuAllowedIds.split(",")) {
            				MenuItem menuAllowed = MenuItem.findById(MenuItem.class, Long.parseLong(menuAllowedId));
            				if(menuAllowed!=null) {
            					DeviceType deviceTypeMenu = null;
            					if(this.getCurrentUser().getHouseType()!=null
        								&& this.getCurrentUser().getHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
        							deviceTypeMenu = DeviceType.findByFieldName(DeviceType.class, "name_upperhouse", menuAllowed.getText(), locale.toString());
            						if(deviceTypeMenu!=null) {
            							continue;
            						}
        						} else if(this.getCurrentUser().getHouseType()!=null
        								&& this.getCurrentUser().getHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
        							deviceTypeMenu = DeviceType.findByFieldName(DeviceType.class, "name_lowerhouse", menuAllowed.getText(), locale.toString());
            						if(deviceTypeMenu!=null) {
            							continue;
            						}
        						} else if(this.getCurrentUser().getHouseType()!=null
        								&& this.getCurrentUser().getHouseType().equals(ApplicationConstants.BOTH_HOUSE)) {
        							//as lowerhouse is default for both house user.. hiding upperhouse menus even if allowed
        							deviceTypeMenu = DeviceType.findByFieldName(DeviceType.class, "name_upperhouse", menuAllowed.getText(), locale.toString());
            						if(deviceTypeMenu!=null) {
            							continue;
            						}
        						}
            				}
            				//add allowed id here
            				filteredBuffer.append(menuAllowed.getId());    
            				filteredBuffer.append(",");
        				}
        				if(filteredBuffer.length()>0){
        					filteredBuffer.deleteCharAt(filteredBuffer.length()-1);   
        					menuAllowedIds = filteredBuffer.toString();
        		        }
    				}    				
    			}         			
    			buffer.append(menuAllowedIds);
            	buffer.append(",");	        	
        	}
        }
        String menuIds="";
        if(buffer.length()>0){
        	buffer.deleteCharAt(buffer.length()-1);
        	menuIds=buffer.toString();
        }     
        String menuXml = MenuItem.getMenuXml(menuIds,locale.toString());
        model.addAttribute("menu_xml", menuXml);
        //adding login time
        model.addAttribute("logintime", FormaterUtil.getDateFormatter(timeFormat, locale.toString()).format(new Date()));
        model.addAttribute("logintime_server", FormaterUtil.formatDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        //adding locale
        model.addAttribute("locale",locale.toString());
        //starting url that will be triggered
        model.addAttribute("startURL",this.getCurrentUser().getStartURL());
        
        // Support URL
        CustomParameter supportURLParam = 
        	CustomParameter.findByName(CustomParameter.class, "SUPPORT_URL", "");
        String supportURL = supportURLParam.getValue();
        model.addAttribute("supportURL", supportURL);
        
        //update static current numbers for all devices on first successful login post deployment
        Device.updateCurrentNumberForDevices();
        
        //flag for checking if this request is redirection to home page
        String redirectedToHomePage = request.getParameter("redirectedToHomePage");
        if(redirectedToHomePage==null || !redirectedToHomePage.equals("yes")) {
             if(isUserAllowedForDashboardView) {
            	populateMemberProfile(model, request, locale);
             	return "member_dashboard";
             } else {
             	return "home";
             }
        } 
        return "home";
    }
    

	//redirect to home page using POST (taken for hiding the flag of request parameter 'redirectedToHomePage')
    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public String redirectToHome(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {  
    	return home(model, request, locale);
    }
    
 // for 403 access denied page
 	@RequestMapping(value = "/403", method = RequestMethod.GET)
 	public String accesssDenied(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {

 		return "login";

 	}
 	
 	/**** Populate High Security Validation Check ****/
	@RequestMapping(value="/high_security_validation_check/init", method=RequestMethod.GET)
	public String initHighSecurityValidationCheck(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		String securedItemId = request.getParameter("securedItemId");
		String eventName = request.getParameter("eventName");
		
		if(securedItemId != null && !(securedItemId.isEmpty())
				&& eventName != null && !(eventName.isEmpty())) {
			try {
				model.addAttribute("securedItemId", securedItemId);
				model.addAttribute("eventName", eventName);
			} 
//			catch (ELSException e) {
//				model.addAttribute("error", e.getParameter());
//			} 
			catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("error", "SOME_ERROR");
			}
		} else {
			model.addAttribute("error", "REQUEST_PARAM_EMPTY");
		}
		
		return "common/high_security_validation_check";
	}
	
	/**** Perform High Security Validation Check ****/
	@RequestMapping(value="/high_security_validation_check", method=RequestMethod.POST)
	public @ResponseBody Boolean performHighSecurityValidationCheck(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		Boolean isHighSecurityValidated = false;
		
		String highSecurityPassword = request.getParameter("highSecurityPassword");
		
		if(highSecurityPassword != null) {
			try {
				AuthUser user=this.getCurrentUser();
		        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");
		        if(highSecurityPassword.equals(credential.getHighSecurityPassword())) {
		        	isHighSecurityValidated = true;
		        }
			} 
//			catch (ELSException e) {
//				return false;
//			} 
			catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("error", "SOME_ERROR");
				return false;
			}
		} else {
			
		}
		
		return isHighSecurityValidated;
	}
	
    private void populateMemberProfile(ModelMap model, HttpServletRequest request, Locale locale) {
    	
		Member member = Member.findMember(this.getCurrentUser().getFirstName(), 
				this.getCurrentUser().getMiddleName(), 
				this.getCurrentUser().getLastName(), 
				this.getCurrentUser().getBirthDate(), locale.toString());
		
		
		if(member != null){
			model.addAttribute("memberName", member.getFullname());
			model.addAttribute("memberPhoto", member.getPhoto());
			model.addAttribute("constituency",member.findConstituency().getDisplayName());
			
			if(member.getBirthDate()!=null){
				model.addAttribute("memberBirthDate",FormaterUtil.formatDateToString(member.getBirthDate(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
			}
			model.addAttribute("memberBirthPlace",member.getBirthPlace());
			if(member.getMemberPartyAssociations() != null && !member.getMemberPartyAssociations().isEmpty()){
				Party party = member.findParty();
				if(party != null){
					model.addAttribute("memberParty", party.getName());
					List<PartySymbol> partySymbols = party.getPartySymbols();
					if(partySymbols != null && !partySymbols.isEmpty()){
						model.addAttribute("memberPartyPhoto",partySymbols.get(0).getSymbol());
					}
					
				}
				
			}
			String  strHouseType = this.getCurrentUser().getHouseType();
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			model.addAttribute("housetype", strHouseType);
			
			List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale.toString());
			model.addAttribute("houseTypes", houseTypes);
			
			Session session = null;
			try {
				session = Session.findLatestSession(houseType);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date currentDate = new Date();
			if(session.getStartDate().equals(currentDate) 
				|| session.getEndDate().equals(currentDate)
				|| (session.getStartDate().before(currentDate) && session.getEndDate().after(currentDate))){
				model.addAttribute("sessionType", session.getType().getId());
				model.addAttribute("sessionYear", session.getYear());
			}
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
			model.addAttribute("sessionTypes", sessionTypes);
			
			List<Integer> years = new ArrayList<Integer>();
			CustomParameter houseFormationYear = 
					CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			if(houseFormationYear != null) {
				Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
				for(int i = session.getYear(); i >= formationYear; i--) {
					years.add(i);
				}
			}
			
			model.addAttribute("years", years);
			
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("memberId", new String[]{member.getId().toString()});
			params.put("locale", new String[]{locale.toString()});
			List report = Query.findReport("LOAD_MINISTER_OF_MEMBER", params);
			if(report != null && !report.isEmpty()){
				Object[] obj = (Object[])report.get(0);
				if(obj[0] != null){
					model.addAttribute("memberDesignation", obj[0]);
				}else{
					List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(member, session, locale.toString());
					for(MemberRole mr : memberRoles){
						if(mr.getType().equalsIgnoreCase(ApplicationConstants.STATE_MINISTER)
							|| mr.getType().equalsIgnoreCase(ApplicationConstants.SPEAKER)
							|| mr.getType().equalsIgnoreCase(ApplicationConstants.CHAIRMAN)
							|| mr.getType().equalsIgnoreCase(ApplicationConstants.DEPUTY_CHAIRMAN)
							|| mr.getType().equalsIgnoreCase(ApplicationConstants.DEPUTY_SPEAKER)
							|| mr.getType().equalsIgnoreCase(ApplicationConstants.LEADER_OF_OPPOSITION)){
							model.addAttribute("memberRole",mr.getName());
							break;
						}else{
							model.addAttribute("memberRole",mr.getName());
						}
					}
				}
			}
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("memberId", new String[]{member.getId().toString()});
			parameters.put("sessionId", new String[]{session.getId().toString()});
			parameters.put("locale", new String[]{locale.toString()});
			List report1 = Query.findReport("MEMBER_DEVICES_COUNT", parameters);
			if(report1 != null && !report1.isEmpty()){
				for(int i=0;i<report1.size();i++){
					Object[] obj = (Object[])report1.get(i);
					for(int j=0;j<obj.length;j++){
						model.addAttribute(obj[2]+"_count", obj[1]);
					}
					
				}
			}
			
			try {
				DocumentLink rotationOrderLink = DocumentLink.findRotationOrderLinkBySession(session);
				if(rotationOrderLink != null){
					model.addAttribute("rotationOrderLink", rotationOrderLink.getUrl());
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
		}
		
	}
}
