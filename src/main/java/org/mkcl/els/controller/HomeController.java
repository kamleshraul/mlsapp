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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.mkcl.els.common.vo.DepartmentDashboardVo;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DocumentLink;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartySymbol;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportLog;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.service.ISecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    /** The Constant ASC. */
    private static final String ASC = "asc";
    
    @Autowired 
	private ISecurityService securityService;

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
            final ModelMap model, 
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Locale locale) {
    	if(this.isAuthenticated()) {    		
    		Object loggedInUser = request.getSession().getAttribute("logged_in_active_user");
            if(loggedInUser!=null) {
            	model.addAttribute("loggedInUser", loggedInUser.toString());    
            	response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            	response.setHeader("Location", "home.htm");
            }
    		return home(model, request, locale);
    	}
        List<ApplicationLocale> supportedLocales = ApplicationLocale.findAll(
                ApplicationLocale.class, "language", ASC, "");
        CustomParameter csptDefaultLocale = CustomParameter.findByName(CustomParameter.class, "DEFAULT_LOCALE", "");
        if (lang != null) {
            model.addAttribute("selectedLocale", lang);
            csptDefaultLocale.setValue(lang);
            csptDefaultLocale.merge();
            model.addAttribute("defaultLocale", csptDefaultLocale.getValue());
        }
        else {        	
        	if(csptDefaultLocale!=null 
        			&& csptDefaultLocale.getValue()!=null && !csptDefaultLocale.getValue().isEmpty()) {
        		model.addAttribute("selectedLocale", csptDefaultLocale.getValue());
        	}
        	model.addAttribute("defaultLocale", locale.toString());
        }
        CustomParameter cpSecretKey = CustomParameter.findByName(CustomParameter.class, "SECRET_KEY_FOR_ENCRYPTION", "");
        if(cpSecretKey != null){
        	model.addAttribute("secret_key", cpSecretKey.getValue());
        }
        CustomParameter cpEncryptionRequired = CustomParameter.findByName(CustomParameter.class, "PASSWORD_ENCRYPTION_REQUIRED", "");
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
        Object loggedInUser = request.getSession().getAttribute("logged_in_active_user");
        if(loggedInUser!=null) {
        	model.addAttribute("loggedInUser", loggedInUser.toString());    
        	response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        	response.setHeader("Location", "home.htm");
        }
        CustomParameter cpNewloginpageFlag = CustomParameter.findByName(CustomParameter.class, "NEW_LOGINPAGE", "");
    	String newloginpageFlag = cpNewloginpageFlag.getValue();
        if(newloginpageFlag!= null && newloginpageFlag.equals("YES")) {
        return "newlogin";
        }
        else
        {
            return "login";
        }
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
    	generateDefaultLocaleOnStartup(locale);	
        //here we will initialize authuser wih locale dependent data such as
        //firstname,middlename,lastname,title and housetype.
        //This housetype will be same as the housetype alloted to user while
        //creating user entry.
        AuthUser user=this.getCurrentUser();
        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");
        /** Find and update Support Log for the current user session **/
        String userAddress = request.getRemoteAddr();
		SupportLog supportLog = SupportLog.findLatest(userAddress);
        if(supportLog!=null) {
        	request.getSession().setAttribute("supportUserName", supportLog.getSupportCredential().getUsername());
        	supportLog.setUserCredential(credential);
        	supportLog.setEntered(true);
        	supportLog.merge();
        	if(request.getSession().getAttribute("logged_in_active_user")==null 
            		|| request.getSession().getAttribute("logged_in_active_user").toString().isEmpty()) {
            	request.getSession().setAttribute("logged_in_active_user", user.getActualUsername());
            }
        } else {     
        	/** Send Multilogin Realtime Notification to Existing User Session (Configurable) **/
            if(request.getSession().getAttribute("logged_in_active_user")==null 
            		|| request.getSession().getAttribute("logged_in_active_user").toString().isEmpty()) {
            	request.getSession().setAttribute("logged_in_active_user", user.getActualUsername());        	
            	if(!credential.isAllowedForMultiLogin()) {
            		CustomParameter csptMultiLoginNotificationEnabled = CustomParameter.findByName(CustomParameter.class, "MULTILOGIN_NOTIFICATION_ELABLED", "");
            		if(csptMultiLoginNotificationEnabled!=null && csptMultiLoginNotificationEnabled.getValue()!=null
            				&& csptMultiLoginNotificationEnabled.getValue().equals("YES")) {
            			NotificationController.sendNotificationFromAdminPage("You have been logged in from somewhere else!!", "Refresh Page to Login Again!!", true, "admin", locale.toString());
            		}   		
            	}
            }
        }                
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
        model.addAttribute("authhousetype", this.getCurrentUser().getHouseType());
        
        //TODO: add code to populate active usergrouptype of authenticatedUser
        
        
        //setting date and time formats to be used.
        String dateFormat= ((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_DATEFORMAT", "")).getValue();
        model.addAttribute("dateFormat",dateFormat);
        String timeFormat=((CustomParameter) CustomParameter.findByName(
                CustomParameter.class, "DATEPICKER_TIMEFORMAT", "")).getValue();
        model.addAttribute("timeFormat",timeFormat);
        /** populate special dash character to be replaced by normal dash character whenever it occurs  **/
        MessageResource specialDashCharacterMsg = MessageResource.findByFieldName(MessageResource.class, "code", "special_dash_character", "en_US");
        if(specialDashCharacterMsg!=null && specialDashCharacterMsg.getValue()!=null) {
        	model.addAttribute("specialDashCharacter",specialDashCharacterMsg.getValue());
        } else {
        	model.addAttribute("specialDashCharacter", "-");
        }
        //right now all menus are visible to all.
        Set<Role> roles=this.getCurrentUser().getRoles();
        StringBuffer buffer=new StringBuffer();
        Boolean isUserAllowedForMemberDashboardView = false; //for showing dashboard post login to allowed users only
        Boolean isUserAllowedForDeptSecretaryDashboardView = false; //for showing dashboard post login to allowed users only
        Boolean isUserAllowedForStatisticalDashboardView =false;
        CustomParameter rolesAllowedForMemberDashBoardViewCP = CustomParameter.findByName(CustomParameter.class, "ROLES_ALLOWED_FOR_MEMBERDASHBOARD_VIEW", "");
        CustomParameter rolesAllowedForDeptSecretaryDashBoardViewCP = CustomParameter.findByName(CustomParameter.class, "ROLES_ALLOWED_FOR_DEPARTMENT_SECRETARY_DASHBOARD_VIEW", "");
        CustomParameter rolesAllowedForStatisticalDashBoardViewCP = CustomParameter.findByName(CustomParameter.class, "ROLES_ALLOWED_FOR_STATISTICAL_DASHBOARD_VIEW", "");
        for(Role i:roles){
        	if(i.getType().equalsIgnoreCase(ApplicationConstants.MEMBER_LOWERHOUSE)
        			|| i.getType().equalsIgnoreCase(ApplicationConstants.MEMBER_UPPERHOUSE)) {
        		model.addAttribute("isMemberLogin", "YES");
        		
        	} else if(i.getType().endsWith(ApplicationConstants.ROLE_DEPARTMENT_USER) && model.get("isDepartmentLogin")==null) {
        		model.addAttribute("isDepartmentLogin", "YES");		
        		
        	} else if(i.getType().endsWith(ApplicationConstants.MINISTER) && model.get("isMinisterLogin")==null) {
        		model.addAttribute("isMinisterLogin", "YES");	
        		
        	} else if((i.getType().endsWith(ApplicationConstants.ROLE_SPEAKER) || i.getType().endsWith(ApplicationConstants.ROLE_CHAIRMAN))
        					&& model.get("isSpeakerOrChairmanLogin")==null) {
        		
        		model.addAttribute("isSpeakerOrChairmanLogin", "YES");
        	}
        	
        	if(rolesAllowedForMemberDashBoardViewCP!=null 
        			&& rolesAllowedForMemberDashBoardViewCP.getValue()!=null
        			&& !rolesAllowedForMemberDashBoardViewCP.getValue().isEmpty()) {
        		for(String allowedRole: rolesAllowedForMemberDashBoardViewCP.getValue().split(",")) {
        			if(i.getType().trim().equalsIgnoreCase(allowedRole.trim())) {
        				isUserAllowedForMemberDashboardView = true;
        				break;
        			}
        		}
        	}
        	if(rolesAllowedForDeptSecretaryDashBoardViewCP!=null 
        			&& rolesAllowedForDeptSecretaryDashBoardViewCP.getValue()!=null
        			&& !rolesAllowedForDeptSecretaryDashBoardViewCP.getValue().isEmpty()) {
        		for(String allowedRole: rolesAllowedForDeptSecretaryDashBoardViewCP.getValue().split(",")) {
        			if(i.getType().trim().equalsIgnoreCase(allowedRole.trim())) {
        				isUserAllowedForDeptSecretaryDashboardView = true;
        				break;
        			}
        		}
        	}
        	if(rolesAllowedForStatisticalDashBoardViewCP!=null 
        			&& rolesAllowedForStatisticalDashBoardViewCP.getValue()!=null
        			&& !rolesAllowedForStatisticalDashBoardViewCP.getValue().isEmpty()) {
        		for(String allowedRole: rolesAllowedForStatisticalDashBoardViewCP.getValue().split(",")) {
        			if(i.getType().trim().equalsIgnoreCase(allowedRole.trim())) {
        				isUserAllowedForStatisticalDashboardView = true;
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
        /** uncomment below lines of code if zeroDigitForLocale is needed **/
//      DecimalFormat nf = (DecimalFormat) FormaterUtil.getNumberFormatterGrouping(locale.toString());
//      if(nf!=null) {
//      	char zeroDigitForLocale = nf.getDecimalFormatSymbols().getZeroDigit();
//        	//int zeroDigitOffsetForLocale = Character.getNumericValue(zeroDigitForLocale);
//        	model.addAttribute("zeroDigitForLocale", zeroDigitForLocale);
//      }        
        //starting url that will be triggered
        model.addAttribute("startURL",this.getCurrentUser().getStartURL());
        
        // Support URL
        CustomParameter supportURLParam = 
        	CustomParameter.findByName(CustomParameter.class, "SUPPORT_URL", "");
        String supportURL = supportURLParam.getValue();
        model.addAttribute("supportURL", supportURL);
        
        model.addAttribute("notifications_visibleMaxCount", ApplicationConstants.NOTIFICATIONS_VISIBLE_MAXIMUM_COUNT);
        
        //update static current numbers for all devices on first successful login post deployment
        Device.updateCurrentNumberForDevices();
        
        //enable/disable push notifications
        CustomParameter csptPushNotificationsEnabled = CustomParameter.findByName(CustomParameter.class, "PUSH_NOTIFICATIONS_ENABLED", "");
        if(csptPushNotificationsEnabled!=null && csptPushNotificationsEnabled.getValue().equals("YES")) {
        	model.addAttribute("pushNotificationsEnabled", "YES");
        } else {
        	model.addAttribute("pushNotificationsEnabled", "NO");
        }
        
        //set and populate 'LATEST ASSSEMBLY HOUSE FORMATION DATE'
        if(ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE=="") {
			CustomParameter csptLatestAssemblyHouseFormationDate = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE, "");
			if(csptLatestAssemblyHouseFormationDate!=null && csptLatestAssemblyHouseFormationDate.getValue()!=null) {
				ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE = csptLatestAssemblyHouseFormationDate.getValue();
			}
		}
        model.addAttribute("latestAssemblyHouseFormationDate", ApplicationConstants.LATEST_ASSSEMBLY_HOUSE_FORMATION_DATE);
        
        model.addAttribute("onlineDepartmentReplyBeginningDate", ApplicationConstants.STARTING_DATE_FOR_FULLY_ONLINE_DEPARTMENT_PROCESSING_OF_DEVICES);
        
        //flag for checking if this request is redirection to home page
        String redirectedToHomePage = request.getParameter("redirectedToHomePage");
        if(redirectedToHomePage==null || !redirectedToHomePage.equals("yes")) {
             if(isUserAllowedForMemberDashboardView) {
            	populateMemberProfile(model, request, locale);
             	return "member_dashboard";
             }else if(isUserAllowedForDeptSecretaryDashboardView){
            	populateDeptSecretaryDashboard(model, request, locale);
              	return "departmentsecretary_dashboard"; 
             }else if(isUserAllowedForStatisticalDashboardView){
            	populateStatisticalDashboard(model, request, locale);
              	return "statisticaldashboard/dashboard"; 
             }else {
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
//				Object supportUserName = request.getSession().getAttribute("supportUserName");
//				if(supportUserName!=null) {
//					Credential credential=Credential.findByFieldName(Credential.class, "username",supportUserName.toString(), "");
//			        if(highSecurityPassword.equals(credential.getPassword())) {
//			        	isHighSecurityValidated = true;
//			        }
//			        if(!isHighSecurityValidated) {
//			        	AuthUser user=this.getCurrentUser();
//				        credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");				        
////				        if(highSecurityPassword.equals(credential.getHighSecurityPassword())) {
////				        	isHighSecurityValidated = true;
////				        }
//				        //TODO: create separate service method for validating high security password
//				        if(securityService.isAuthenticated(highSecurityPassword, credential.getHighSecurityPassword())) {
//				        	isHighSecurityValidated = true;
//				        }
//			        }
//				} else {
//					AuthUser user=this.getCurrentUser();
//			        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");
//			        if(highSecurityPassword.equals(credential.getHighSecurityPassword())) {
//			        	isHighSecurityValidated = true;
//			        }
//				}	
				
				String securedItemId = request.getParameter("securedItemId");
				String eventName = request.getParameter("eventName");
				if(securedItemId!=null && securedItemId.equalsIgnoreCase("updateDecisionForMotions")) {
					//System.out.println("eventName: " + eventName);
					CustomParameter csptSecuredItemId = CustomParameter.findByName(CustomParameter.class, securedItemId.toUpperCase()+"_SECURITY_KEY", "");
					if(csptSecuredItemId!=null && csptSecuredItemId.getValue()!=null) {
						if(securityService.isAuthenticated(highSecurityPassword, csptSecuredItemId.getValue())) {
				        	isHighSecurityValidated = true;
				        }
					}
				} else {
					AuthUser user=this.getCurrentUser();
			        Credential credential=Credential.findByFieldName(Credential.class, "username",user.getUsername(), "");	
			        //TODO: create separate service method for validating high security password
			        if(securityService.isAuthenticated(highSecurityPassword, credential.getHighSecurityPassword())) {
			        	isHighSecurityValidated = true;
			        }					
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
    
    private void populateDeptSecretaryDashboard(ModelMap model, HttpServletRequest request, Locale locale) {
    	 List<HouseType> houseTypes = HomeController.getHouseTypes(locale.toString());
    	 model.addAttribute("houseTypes", houseTypes);
    	 
    	 List<SessionType> sessionTypes = HomeController.getSessionTypes(locale.toString());
    	 model.addAttribute("sessionTypes", sessionTypes);
    	 
 		Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);
		List<String> years = HomeController.getSessionYears(latestYear, locale.toString());
    	 model.addAttribute("years", years);
    	 
    	List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "name", ASC, locale.toString());
    	model.addAttribute("deviceTypes", deviceTypes);
    	 try {
			List<SubDepartment> subDepartments = SubDepartment.findAllSubDepartments(locale.toString());
			 model.addAttribute("subDepartments", subDepartments);
    	 } catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	 List<DepartmentDashboardVo> departmentDeviceCounts = new ArrayList<DepartmentDashboardVo>();
    	 Map<String, String[]> parameters = new HashMap<String, String[]>();
         parameters.put("locale", new String[]{locale.toString()});
         parameters.put("sessionType", new String[]{""});
         parameters.put("sessionYear", new String[]{""});
         parameters.put("houseType", new String[]{""});
         parameters.put("deviceType", new String[]{""});
         parameters.put("subdepartment", new String[]{""});
         List result = Query.findReport("DEPARTMENT_DEVICES_COUNT", parameters);
         for(int i=0;i<result.size();i++){
        	 Object[] row = (Object[])result.get(i);
        		DepartmentDashboardVo departmentDeviceCount = new DepartmentDashboardVo();
              	 departmentDeviceCount.setSubdepartment(row[0].toString());
              	 //Pending Count
              	 departmentDeviceCount.setPendingCount(Integer.parseInt(row[1].toString()));
              	 //Completed Count
              	 departmentDeviceCount.setCompletedCount(Integer.parseInt(row[2].toString()));
              	 //Timeout Count
              	 departmentDeviceCount.setTimeoutCount(Integer.parseInt(row[3].toString()));
              	 //Total Count
              	 departmentDeviceCount.setTotalCount(Integer.parseInt(row[4].toString()));
              	//House Type
               	 departmentDeviceCount.setHouseType(row[5].toString());
               	 //Session Type
               	 departmentDeviceCount.setSessionType(row[6].toString());
               	 //Session Year
               	 departmentDeviceCount.setSessionYear(row[7].toString());
               	 //Device Type
               	departmentDeviceCount.setDeviceType(row[8].toString());
              	 
              	 departmentDeviceCounts.add(departmentDeviceCount);
        }
         model.addAttribute("result", departmentDeviceCounts);
		
	}
    
    private void populateStatisticalDashboard(ModelMap model, HttpServletRequest request, Locale locale) {
		// TODO Auto-generated method stub
		
	}
    
    public static List<HouseType> getHouseTypes(final String locale) {
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
		
		return houseTypes;
	}
    
    public static List<SessionType> getSessionTypes(final String locale) {
		List<SessionType> sessionTypes = 
				SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale);
		return sessionTypes;
	}
    
	public static List<String> getSessionYears(final Integer latestYear, final String locale)  {
		List<String> years = new ArrayList<String>();
		
		CustomParameter houseFormationYear = 
				CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		if(houseFormationYear != null) {
			Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
			for(int i = latestYear; i >= formationYear; i--) {
				years.add(FormaterUtil.formatNumberNoGrouping(i, locale));
			}
		}
		
		return years;
	}
	
	private void generateDefaultLocaleOnStartup(final Locale locale) {
		CustomParameter csptDefaultLocale = CustomParameter.findByName(CustomParameter.class, "DEFAULT_LOCALE", "");
    	if(csptDefaultLocale!=null) {
    		if(csptDefaultLocale.getValue()==null || csptDefaultLocale.getValue().isEmpty()) { //possible at startup
        		if(locale!=null) {
        			csptDefaultLocale.setValue(locale.toString());
        		} else {
        			csptDefaultLocale.setValue(ApplicationConstants.DEFAULT_LOCALE);
        		}
        		csptDefaultLocale.merge();
        	}
    	}
	}
}
