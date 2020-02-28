/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.wf.WorkflowController.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.controller.wf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/workflow")
public class WorkflowController extends BaseController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** The process service. */
	@Autowired
	private IProcessService processService;

	//==================== Deployment Methods ====================

	/**
	 * Deploy module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/module", method=RequestMethod.GET)
	public String deployModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.module(model, request, locale);
	}

	/**
	 * Deploy list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/list", method=RequestMethod.GET)
	public String deployList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	/**
	 * Deploy new.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/new", method=RequestMethod.GET)
	public String deployNew(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		model.addAttribute("type", "");
		return this.getResourcePath(request);
	}

	/**
	 * Deploy create.
	 *
	 * @param model the model
	 * @param request the request
	 * @param docTag the doc tag
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/{docTag}/create", method=RequestMethod.GET)
	public @ResponseBody String deployCreate(final ModelMap model,
			final HttpServletRequest request,
			final @PathVariable("docTag") String docTag,
			final Locale locale) {
		Document document;
		try {
			document = Document.findByTag(docTag);
			if(document != null) {
				InputStream is = new ByteArrayInputStream(document.getFileData());
				this.processService.deploy(document.getOriginalFileName(), is);
				try {
					is.close();
				}
				catch (IOException e) {
					this.logger.error(e.getMessage());
				}
				// The process has been successfully deployed. The document object
				// is no longer necessary, so remove it from the datastore.
				document.remove();
			}
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
		}
		return "";
	}

	// Deletes the process definition, cascades deletion to process instances,
	// history process instances and jobs.
	/**
	 * Deploy delete.
	 *
	 * @param model the model
	 * @param request the request
	 * @param procDefId the proc def id
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/{procDefId}/delete", method=RequestMethod.DELETE)
	public String deployDelete(final ModelMap model,
			final HttpServletRequest request,
			final @PathVariable("procDefId") String procDefId,
			final Locale locale) {
		ProcessDefinition processDefinition = processService.findProcessDefinitionById(procDefId);
		processService.undeploy(processDefinition, true);
		return "info";
	}

	public static String isUserAllowedForURL(HttpServletRequest request, UserGroup userGroup, String locale){
		
		String url = request.getRequestURL().toString();
		Set<Role> uRoles = userGroup.getCredential().getRoles();
		StringBuffer strMenus = new StringBuffer();
		
		for(Role r : uRoles){
			String menusAllowed = r.getMenusAllowed();
			if(menusAllowed != null && !menusAllowed.isEmpty()){
				String[] menusTopLevel = menusAllowed.split("##"); 
				for(String m : menusTopLevel){
					String[] menusSecondLevel = m.split(",");
					for(String menuId : menusSecondLevel){
						MenuItem mI = MenuItem.findById(MenuItem.class, new Long(menuId));
						if(mI != null && mI.getUrl() != null && !mI.getUrl().isEmpty()){
							strMenus.append(mI.getUrl()+",");
						}
					}
				}
			}
		}
		
		return strMenus.toString();
	}
	//==================== My Task Methods ====================

	/**
	 * My tasks module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 * @throws ELSException 
	 */
	@RequestMapping(value="myTasks/module", method=RequestMethod.GET)
	public String myTasksModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale applocale) throws ELSException {
		String errorpage=this.getResourcePath(request).replace("module","error");
		String locale=applocale.toString();
		model.addAttribute("moduleLocale", locale);
		/**** This is for getting only the tasks of current user ****/
		model.addAttribute("assignee",this.getCurrentUser().getActualUsername());		
		/**** House Types ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType=this.getCurrentUser().getHouseType();
		if(houseType.equals("lowerhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("upperhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("bothhouse")){
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
		}
		model.addAttribute("houseTypes", houseTypes);
		if(houseType.equals("bothhouse")){
			houseType="lowerhouse";
		}
		model.addAttribute("houseType",houseType);		
		/**** Session Types ****/
		List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
		/**** Latest Session of a House Type ****/
		HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
		Session lastSessionCreated = null;
		try {
			lastSessionCreated = Session.findLatestSession(authUserHouseType);
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
		}
		/*** Session Year and Session Type ****/
		Integer year=new GregorianCalendar().get(Calendar.YEAR);
		if(lastSessionCreated.getId()!=null){
			year=lastSessionCreated.getYear();
			model.addAttribute("sessionType",lastSessionCreated.getType().getId());
			String groupsAllowed=this.getCurrentUser().getGroupsAllowed();
			if(groupsAllowed!=null && !groupsAllowed.isEmpty()){
				String groups[]=groupsAllowed.split(",");
				List<MasterVO> groupNumberVOs=new ArrayList<MasterVO>();
				for(int i=0;i<groups.length;i++){
					MasterVO groupNumber=new MasterVO();
					try {
						Group group = Group.findByNumberHouseTypeSessionTypeYear(Integer.parseInt(groups[i]), lastSessionCreated.getHouse().getType(), lastSessionCreated.getType(), year);
						if(group!=null) {
							groupNumber.setName(FormaterUtil.formatNumbersInGivenText(groups[i], locale));
							groupNumber.setId(group.getId());
							groupNumberVOs.add(groupNumber);
						}						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						model.addAttribute("errorcode","nogroupentriesfound");
						return errorpage;
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						model.addAttribute("errorcode","nogroupentriesfound");
						return errorpage;
					}
					
				}
				model.addAttribute("groups", groupNumberVOs);
			}
		}else{
			model.addAttribute("errorcode","nosessionentriesfound");
			return errorpage;
		}
		model.addAttribute("sessionTypes",sessionTypes);
		/**** Years ****/
		CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		List<String> years=new ArrayList<String>();
		if(houseFormationYear!=null){
			Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
			for(int i=year;i>=formationYear;i--){
				years.add(FormaterUtil.getNumberFormatterNoGrouping(locale).format(i));
			}
		}else{
			model.addAttribute("errorcode", "houseformationyearnotset");
			return errorpage;
		}
		model.addAttribute("years",years);
		model.addAttribute("sessionYear",year);

		/**** Device Types. ****/
		//List<DeviceType> allDeviceTypes = DeviceType.findAll(DeviceType.class,"priority",ApplicationConstants.ASC, locale.toString());
		
		Credential cr = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), "");
		
		List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
		//UserGroupType userGroupType = null;
		List<DeviceType> deviceTypes=null;
		String alldeviceTypeNameParam="";
		for(UserGroup ug : userGroups){
			UserGroup userGroup = UserGroup.findActive(cr, ug.getUserGroupType(), new Date(), locale.toString());
			if(userGroup != null){
				/**** Authenticated User's usergroup and usergroupType ****/
				String userGroupType = userGroup.getUserGroupType().getType();			
				model.addAttribute("usergroup", userGroup.getId());
				model.addAttribute("usergroupType", userGroupType);
				
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(ug);
				String deviceTypeNameParam= parameters.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale);
				if(deviceTypeNameParam != null && ! deviceTypeNameParam.equals("")) {
					//alldeviceTypeNameParam.concat(deviceTypeNameParam);
					alldeviceTypeNameParam=alldeviceTypeNameParam+deviceTypeNameParam;
					//deviceTypes=DeviceType.findAllowedTypesForUser(deviceTypeNameParam, "##", locale);
				}
			
			}
		}
		deviceTypes=DeviceType.findAllowedTypesForUser(alldeviceTypeNameParam, "##", locale);
		
		if(deviceTypes == null)
		{
				deviceTypes=DeviceType.findAll(DeviceType.class,"priority",ApplicationConstants.ASC, locale.toString());
		}
		//UserGroup ug = UserGroup.findActive(cr, new Date(), locale);
		
		
		
		
		//List<DeviceType> deviceTypes = allDeviceTypes;//DeviceType.findAllowedTypesForUser(ug,allDeviceTypes,locale.toString());
		
		model.addAttribute("deviceTypes", deviceTypes);
		List<MasterVO> deviceTypeVOs = new ArrayList<MasterVO>();
		for(DeviceType deviceType: deviceTypes) {
			MasterVO deviceTypeVO = new MasterVO();
			deviceTypeVO.setId(deviceType.getId());
			deviceTypeVO.setType(deviceType.getType());
			deviceTypeVO.setName(deviceType.getName());
			if(houseType!=null && houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
				deviceTypeVO.setDisplayName(deviceType.getName_lowerhouse());
			} else if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
				deviceTypeVO.setDisplayName(deviceType.getName_upperhouse());
			} else {
				deviceTypeVO.setDisplayName(deviceType.getName());
			}					
			deviceTypeVOs.add(deviceTypeVO);
		}
		model.addAttribute("deviceTypeVOs", deviceTypeVOs);
		/**** Default Value ****/	
		DeviceType selectedDeviceType=deviceTypes.get(0);
		model.addAttribute("selectedDeviceType", selectedDeviceType.getType());
		
		/**** Session Dates for Devices like AdjournmentMotions. ****/
		List<Date> sessionDates = lastSessionCreated.findAllSessionDatesHavingNoHoliday();
		model.addAttribute("sessionDates", this.populateDateListUsingCustomParameterFormat(sessionDates, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", locale));		
		/**** Default Value ****/
		Date defaultAdjourningDate = null;
		defaultAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(lastSessionCreated, false);
		model.addAttribute("defaultAdjourningDate", FormaterUtil.formatDateToString(defaultAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT));
		

		
		/**** Types of Workflows ****/
		/**** added by sandeep singh(jan 29 2013) ****/
		/**** Custom Parameter To Determine The Usergroup and usergrouptype of qis users ****/
		String strUserGroup = request.getParameter("usergroup");
		//List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
		List<Status> workflowTypes=new ArrayList<Status>();
		List<SubDepartment> subdepartments = new ArrayList<SubDepartment>();
 		/**** Workflows for a particular device type will be visible 
		 * only if the user has been assigned that device type while
		 * creating user group for a particular user.****/
		String url = request.getRequestURL().toString();
		if(userGroups!=null){
			if(!userGroups.isEmpty()){				
				for(UserGroup i:userGroups){
					UserGroup userGroup=UserGroup.findById(UserGroup.class,i.getId());	
					String userGroupDeviceType=userGroup.getParameterValue(ApplicationConstants.DEVICETYPE_KEY+"_"+locale);
					//userGroupDeviceType.contains(selectedDeviceType.getName())
					//changed for the purpose in case when there is only one device is allocated
					//then it fails as the by default selected device is first in the 
					//deviceType List
					
					String strMenus = WorkflowController.isUserAllowedForURL(request, userGroup, locale);
					
					if(!strMenus.contains(url.substring(url.indexOf("els/")+"els/".length()))){
						model.addAttribute("errorcode","user_not_allowed");
						return errorpage;
					}
					
					String userGroupSubdepartments = userGroup.getParameterValue(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale);
					
					String[] strSubdepartments = userGroupSubdepartments.split("##");
					for(String s : strSubdepartments){
						SubDepartment subdepartment = SubDepartment.findByName(SubDepartment.class, s, locale);
						if( subdepartment != null && !subdepartments.contains(subdepartment)){
							subdepartments.add(subdepartment);
						}
					}
					
					DeviceType newSelectedDeviceType = isAllowedDevice(deviceTypes, userGroupDeviceType); 
					if(newSelectedDeviceType != null){
						String userGroupType = i.getUserGroupType().getType();
						/**** Status Allowed ****/
						CustomParameter allowedWorkflowTypes=CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_"+newSelectedDeviceType.getType().toUpperCase()+"_"+userGroupType.toUpperCase(), "");
						if(allowedWorkflowTypes!=null){
							try {
									List<Status> workflowTypesForUsergroup=Status.findStatusContainedIn(allowedWorkflowTypes.getValue(), locale);
									workflowTypes.addAll(workflowTypesForUsergroup);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
						}
						if(workflowTypes.isEmpty()) {
							CustomParameter defaultAllowedWorkflowTypes=CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT", "");
							if(defaultAllowedWorkflowTypes!=null){
								try {
									workflowTypes=Status.findStatusContainedIn(defaultAllowedWorkflowTypes.getValue(), locale);
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
							}else{
								model.addAttribute("errorcode","mytask_grid_workflow_types_allowed_by_default_notset");
								return errorpage;
							}
						}
						model.addAttribute("workflowTypes",workflowTypes);						
					}else{
						Set<Role> roles = userGroup.getCredential().getRoles();
						for(Role r : roles){
							if(r != null){
								if(r.getType().startsWith("EDIS")){
									model.addAttribute("usergroup", userGroup.getId());
									model.addAttribute("usergroupType", userGroup.getUserGroupType().getType());
								}
							}
						}
					}
				}		
				model.addAttribute("subdepartments", subdepartments);
			}else{
				model.addAttribute("errorcode","current_user_has_no_usergroups");
				return errorpage;
			}
		}else{
			model.addAttribute("errorcode","current_user_has_no_usergroups");
			return errorpage;
		}
		return this.getResourcePath(request);
	}

	/**
	 * My tasks list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="myTasks/list", method=RequestMethod.GET)
	public String myTasksList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	/**
	 * Follow a convention where URL path corresponds to JSP folder structure.
	 * If a JSP exists with following folder structure
	 * question
	 * |- starred
	 * |- process.jsp
	 * then the URL path must be question/starred/process.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @param taskId the task id
	 * @param locale the locale
	 */
	@RequestMapping(value="myTasks/{worklowdetailsId}/process", method=RequestMethod.GET)
	public void myTasksProcess(final ModelMap model,
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @PathVariable("worklowdetailsId") Long worklowdetailsId,
			final Locale locale) {
		/**** Here workflowdetails contains all the information related to a task ****/
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,worklowdetailsId);
		request.setAttribute("workflowdetails", workflowDetails.getId());
		try {
			request.getRequestDispatcher("/"+workflowDetails.getUrlPattern()).forward(request, response);
		}
		catch (ServletException e) {
			this.logger.error(e.getMessage());
		}
		catch (IOException e) {
			this.logger.error(e.getMessage());
		}
	}

	//==================== Group Task Methods ===================

	/**
	 * Group tasks module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/module", method=RequestMethod.GET)
	public String groupTasksModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.getResourcePath(request);
	}

	/**
	 * Group tasks list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/list", method=RequestMethod.GET)
	public String groupTasksList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	// TODO
	/**
	 * Group tasks claim.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @param taskId the task id
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/{taskId}/claim", method=RequestMethod.GET)
	public String groupTasksClaim(final ModelMap model,
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @PathVariable("taskId") String taskId,
			final Locale locale) {
		return null;
	}

	//==================== Internal Methods ===================

	/**
	 * Module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	private String module(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {

		return this.getResourcePath(request);
	}

	/**
	 * List.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	private String list(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {		
		String resourcePath = this.getResourcePath(request);
		String urlPattern = resourcePath.split("\\/list")[0];
		String newurlPattern=modifyURLPattern(urlPattern,request,model,locale.toString());
		Grid grid;
		try {
			grid = Grid.findByDetailView(newurlPattern, locale.toString());
			model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
		}
		return resourcePath;
	}

	private String modifyURLPattern(final String urlPattern, 
			final HttpServletRequest request, 
			final ModelMap model, 
			final String string) {
		String newUrlPattern=urlPattern;
		String deviceTypeForGrid = request.getParameter("deviceTypeForGrid");
		String strCurrentUserGroupType = request.getParameter("currentusergroupType");
		if(deviceTypeForGrid!=null && !deviceTypeForGrid.isEmpty() && !deviceTypeForGrid.equals("-")) {
			if(deviceTypeForGrid.equals(ApplicationConstants.BILLAMENDMENT_MOTION)) {
				newUrlPattern=urlPattern+"?devicetype="+ApplicationConstants.BILLAMENDMENT_MOTION;
			}else if(deviceTypeForGrid.equals(ApplicationConstants.ADJOURNMENT_MOTION)) {
				newUrlPattern=urlPattern+"?devicetype="+ApplicationConstants.ADJOURNMENT_MOTION;
				if(strCurrentUserGroupType!=null && !strCurrentUserGroupType.isEmpty()) {
					if(strCurrentUserGroupType.equals(ApplicationConstants.DEPARTMENT)
							||strCurrentUserGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
						newUrlPattern = newUrlPattern + "&usergroup="+ApplicationConstants.DEPARTMENT;
					}
				}				
			}else if(deviceTypeForGrid.equals(ApplicationConstants.RULESSUSPENSION_MOTION)) {
				newUrlPattern=urlPattern+"?devicetype="+ApplicationConstants.RULESSUSPENSION_MOTION;
			}
			else if(deviceTypeForGrid.equals(ApplicationConstants.SPECIAL_MENTION_NOTICE)) {
				newUrlPattern=urlPattern+"?devicetype="+ApplicationConstants.SPECIAL_MENTION_NOTICE;
			}else {
				if(strCurrentUserGroupType!=null && !strCurrentUserGroupType.isEmpty()) {
					if(strCurrentUserGroupType.equals(ApplicationConstants.DEPARTMENT)
							||strCurrentUserGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
						newUrlPattern = urlPattern + "?usergroup="+ApplicationConstants.DEPARTMENT;
					}
				}				
			}			
		} 
		String statusForMyTaskGrid = request.getParameter("status");
		if(statusForMyTaskGrid!=null && !statusForMyTaskGrid.isEmpty() && !statusForMyTaskGrid.equals("-")) {
			if(newUrlPattern.equals(urlPattern)) {
				newUrlPattern=newUrlPattern+"?status="+statusForMyTaskGrid;
			} else {
				newUrlPattern=newUrlPattern+"&status="+statusForMyTaskGrid;
			}			
		}
		return newUrlPattern;
	}

	/**
	 * Gets the resource path.
	 *
	 * @param request the request
	 * @return the resource path
	 */
	private String getResourcePath(final HttpServletRequest request) {
		String resourcePath = request.getServletPath().replaceFirst("\\/", "");
		return resourcePath;
	}

	
	private DeviceType isAllowedDevice(List<DeviceType> devices, String userGroupDevice){
		DeviceType retVal = null;
		for(DeviceType device : devices){
			if(userGroupDevice.contains(device.getName())){
				retVal = device;
				break;
			}
		}
		
		return retVal;
	}
}
