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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
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
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
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

	//==================== My Task Methods ====================

	/**
	 * My tasks module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="myTasks/module", method=RequestMethod.GET)
	public String myTasksModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale applocale) {
		String errorpage=this.getResourcePath(request).replace("module","error");
		String locale=applocale.toString();
		/**** This is for getting only the tasks of current user ****/
		model.addAttribute("assignee",this.getCurrentUser().getActualUsername());
		/**** Device Types ****/
		List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class,"name",ApplicationConstants.ASC, locale.toString());
		model.addAttribute("deviceTypes", deviceTypes);
		DeviceType selectedDeviceType=deviceTypes.get(0);
		model.addAttribute("selectedDeviceType", selectedDeviceType.getType());
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
					groupNumber.setName(FormaterUtil.formatNumbersInGivenText(groups[i], locale));
					groupNumberVOs.add(groupNumber);
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
		/**** Types of Workflows ****/
		/**** added by sandeep singh(jan 29 2013) ****/
		/**** Custom Parameter To Determine The Usergroup and usergrouptype of qis users ****/			
		List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
		List<Status> workflowTypes=new ArrayList<Status>();
		/**** Workflows for a particular device type will be visible 
		 * only if the user has been assigned that device type while
		 * creating user group for a particular user.****/
		if(userGroups!=null){
			if(!userGroups.isEmpty()){				
				for(UserGroup i:userGroups){
					UserGroup userGroup=UserGroup.findById(UserGroup.class,i.getId());					
					String userGroupDeviceType=userGroup.getParameterValue(ApplicationConstants.DEVICETYPE_KEY+"_"+locale);
					//userGroupDeviceType.contains(selectedDeviceType.getName())
					//changed for the purpose in case when there is only one device is allocated
					//then it fails as the by default selected device is first in the 
					//deviceType List
					DeviceType newSelectedDeviceType = isAllowedDevice(deviceTypes, userGroupDeviceType); 
					if(newSelectedDeviceType != null){
						/**** Authenticated User's usergroup and usergroupType ****/
						String userGroupType=i.getUserGroupType().getType();			
						model.addAttribute("usergroup", userGroup.getId());
						model.addAttribute("usergroupType", userGroup.getUserGroupType().getType());
						
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
		Grid grid;
		try {
			grid = Grid.findByDetailView(urlPattern, locale.toString());
			model.addAttribute("gridId", grid.getId());
		} catch (ELSException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getParameter());
		}
		return resourcePath;
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
