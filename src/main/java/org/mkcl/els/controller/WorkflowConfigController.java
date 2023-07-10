package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/workflowconfig")
public class WorkflowConfigController extends GenericController<WorkflowConfig> {

	@Override
	protected void populateModule(final ModelMap model,
			final HttpServletRequest request, final String locale,
			final AuthUser currentUser) {
		try{
			/**** Device Types ****/
			List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class,"type", ApplicationConstants.ASC, locale);
			model.addAttribute("deviceTypes", deviceTypes);
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType = this.getCurrentUser().getHouseType();
			if (houseType.equals("lowerhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType, "name", ApplicationConstants.ASC, locale);
			} else if (houseType.equals("upperhouse")) {
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",
						houseType, "name", ApplicationConstants.ASC, locale);
			} else if (houseType.equals("bothhouse")) {
				houseTypes = HouseType.findAll(HouseType.class, "type",
						ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if (houseType.equals("bothhouse")) {
				houseType = "lowerhouse";
			}
			model.addAttribute("houseType", houseType);
			String rootDir = request.getSession().getServletContext().getRealPath("/");
			logger.error("Root Directory Path: " + rootDir); //for testing.. remove later
			System.out.print("Root Directory Path: " + rootDir);
//			if(rootDir!=null && rootDir.contains("\\")) {
//				String[] rootDirArr = rootDir.split("\\");
//				if(rootDirArr.length>1) {
//					
//				}
//			}
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	@Override
	protected void populateNew(final ModelMap model,
			final WorkflowConfig domain, final String locale,
			final HttpServletRequest request) {
		
		try{
			/**** locale ****/
			domain.setLocale(locale);
			/**** HouseType ****/
			String strHouseType = request.getParameter("houseType");
			if (strHouseType != null) {
				if (!strHouseType.isEmpty()) {
					model.addAttribute("houseType", strHouseType);
				}
			}
			/**** workflows ****/
			List<Workflow> workflows = Workflow.findAll(Workflow.class, "name",
					ApplicationConstants.ASC, locale);
			model.addAttribute("workflows", workflows);
			/**** usergroups ****/
			List<UserGroupType> userGroupTypes = UserGroupType.findAll(
					UserGroupType.class, "name", ApplicationConstants.ASC, locale);
			model.addAttribute("userGroupTypes", userGroupTypes);
			/**** device types ****/
			List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class,
					"name", ApplicationConstants.ASC, locale);
			model.addAttribute("deviceTypes", deviceTypes);
			String strdeviceType = request.getParameter("deviceType");
			if (strdeviceType != null) {
				if (!strdeviceType.isEmpty()) {
					DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(strdeviceType));
					domain.setDeviceType(deviceType);
				}
			}
			/**** workflow actor count ****/
			model.addAttribute("workflowactorCount", 0);
			/**** Created On ****/
			model.addAttribute("createdOn", FormaterUtil.getDateFormatter("en_US").format(new Date()));
			
			model.addAttribute("isDeviceTypeEmpty", true);
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	@Override
	protected void populateEdit(final ModelMap model,
			final WorkflowConfig domain, final HttpServletRequest request) {
		try{
			String locale = domain.getLocale();
			/**** HouseTypes ****/
			if(domain.getHouseType() != null){
				model.addAttribute("houseType", domain.getHouseType().getId());
			}
			
			/**** workflows ****/
			List<Workflow> workflows = Workflow.findAll(Workflow.class, "name",
					ApplicationConstants.ASC, locale);
			model.addAttribute("workflows", workflows);
			/**** usergroups ****/
			List<UserGroupType> userGroupTypes = UserGroupType.findAll(UserGroupType.class, "name", ApplicationConstants.ASC, locale);
			model.addAttribute("userGroupTypes", userGroupTypes);
			/**** device types ****/
			List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class,"name", ApplicationConstants.ASC, locale);
			model.addAttribute("deviceTypes", deviceTypes);
			/**** Created On ****/
			if (domain.getCreatedOn() != null) {
				model.addAttribute("createdOn",FormaterUtil.getDateFormatter("en_US").format(domain.getCreatedOn()));
			}
			/**** workflow actors ****/
			model.addAttribute("workflowactors", domain.getWorkflowactors());
			model.addAttribute("workflowactorCount", domain.getWorkflowactors().size());
			
			model.addAttribute("module",domain.getModule());
			
			DeviceType deviceType = domain.getDeviceType();
	        if(deviceType == null) {
	        	model.addAttribute("isDeviceTypeEmpty", true);
	        }
	        else {
	        	model.addAttribute("isDeviceTypeEmpty", false);
	        }
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	private void populateWorkflowActors(final WorkflowConfig domain,
			final HttpServletRequest request, final BindingResult result) {
		try{
			List<WorkflowActor> workflowActors = new ArrayList<WorkflowActor>();
			Integer workflowactorCount = Integer.parseInt(request.getParameter("workflowactorCount"));
			for (int i = 1; i <= workflowactorCount; i++) {
				WorkflowActor workflowActor = null;
	
				String strUserGroupType = request.getParameter("workflowactorName"
						+ i);
				if (strUserGroupType != null) {
					workflowActor = new WorkflowActor();
					UserGroupType userGroupType = UserGroupType.findById(UserGroupType.class, Long.parseLong(strUserGroupType));
					workflowActor.setUserGroupType(userGroupType);
				}
	
				String id = request.getParameter("workflowactorId" + i);
				if (id != null) {
					if (!id.isEmpty()) {
						workflowActor.setId(Long.parseLong(id));
					}
				}
	
				String level = request.getParameter("workflowactorLevel" + i);
				if (level != null) {
					if (!level.isEmpty()) {
						workflowActor.setLevel(Integer.parseInt(level));
					}
				}
	
				String version = request.getParameter("workflowactorVersion" + i);
				if (version != null) {
					if (!version.isEmpty()) {
						workflowActor.setVersion(Long.parseLong(version));
					}
				}
	
				String locale = request.getParameter("workflowactorLocale" + i);
				if (locale != null) {
					if (!locale.isEmpty()) {
						workflowActor.setLocale(locale);
					}
				}
				workflowActors.add(workflowActor);
			}
			domain.setWorkflowactors(workflowActors);
		}catch (Exception e) {
			String message = null;
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
				//model.addAttribute("error", ((ELSException)e).getParameter());
			}else{
				message = e.getMessage();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			//model.addAttribute("error", message);
			e.printStackTrace();
		}
	}

	@Override
	protected void preValidateCreate(final WorkflowConfig domain,
			final BindingResult result, final HttpServletRequest request) {
		populateWorkflowActors(domain, request, result);
	}

	@Override
	protected void preValidateUpdate(final WorkflowConfig domain,
			final BindingResult result, final HttpServletRequest request) {
		populateWorkflowActors(domain, request, result);

	}

	@Override
	protected void populateAfterCreate(ModelMap model, WorkflowConfig domain,
			HttpServletRequest request) throws Exception {
		if(domain.getDeviceType()!=null && domain.getIsLocked()) {
			List<WorkflowConfig> lockedWorkflowConfigs 
					= WorkflowConfig.findLockedWorkflowConfigOfGivenWOrkflowTypeForGivenDeviceType(domain.getHouseType(), domain.getDeviceType(), domain.getWorkflow().getType(), domain.getLocale());
			for(WorkflowConfig wc: lockedWorkflowConfigs) {
				if(!wc.getId().equals(domain.getId())) {
					wc.setIsLocked(false);
					wc.merge();
				}
			}
		}
	}

	@Override
	protected void populateAfterUpdate(ModelMap model, WorkflowConfig domain,
			HttpServletRequest request) throws Exception {
		if(domain.getDeviceType()!=null && domain.getIsLocked()) {
			List<WorkflowConfig> lockedWorkflowConfigs 
					= WorkflowConfig.findLockedWorkflowConfigOfGivenWOrkflowTypeForGivenDeviceType(domain.getHouseType(), domain.getDeviceType(), domain.getWorkflow().getType(), domain.getLocale());
			for(WorkflowConfig wc: lockedWorkflowConfigs) {
				if(!wc.getId().equals(domain.getId())) {
					wc.setIsLocked(false);
					wc.merge();
				}
			}
		}
	}

	@Transactional
	@RequestMapping(value = "/{workflowconfigId}/{workflowactorid}/delete", method = RequestMethod.DELETE)
	public @ResponseBody
	String deleteWorkflowActor(
			final @PathVariable("workflowconfigId") Long workflowconfigId,
			final @PathVariable("workflowactorid") Long workflowactorId,
			final ModelMap model, final HttpServletRequest request) {
		WorkflowConfig workflowConfig = WorkflowConfig.findById(WorkflowConfig.class, workflowconfigId);
		Boolean status = false;
		if (workflowConfig.getIsLocked() == false) {
			status = WorkflowConfig.removeActor(workflowconfigId,workflowactorId);
		}
		if (status) {
			return "SUCCESS";
		} else {
			return "FAILED";
		}
	}
	
	@Transactional
	@RequestMapping(value = "/wfc/{workflowconfigId}/delete", method = RequestMethod.DELETE)
	public @ResponseBody
	String deleteWorkflowConfig(
			@PathVariable(value="workflowconfigId") Long workflowconfigId,
			final ModelMap model, final HttpServletRequest request) {
		Role role = null;
		Boolean status = false;
		for(Role r : this.getCurrentUser().getRoles()){
			if(r.getType().equals("SUPER_ADMIN")){
				role = r;
				break;
			}
		}
		if(role.getType().equals("SUPER_ADMIN")){
			WorkflowConfig workflowConfig = WorkflowConfig.findById(WorkflowConfig.class, workflowconfigId);
			if (workflowConfig.getIsLocked() == false) {
				status = workflowConfig.remove();
			}
		}
		if (status) {
			return "SUCCESS";
		} else {
			return "FAILED";
		}
	}

	@Override
	public String delete(final Long id, final ModelMap model,
			final HttpServletRequest request) {
		WorkflowConfig workflowConfig = WorkflowConfig.findById(WorkflowConfig.class, id);
		if (workflowConfig.getIsLocked() == false) {
			workflowConfig.remove();
			model.addAttribute("flag", "SUCCESS");
		} else {
			model.addAttribute("flag", "SUCCESS");
		}
		return "deleteinfo";
	}

	@Override
	protected void customValidateCreate(final WorkflowConfig domain,
			final BindingResult result, final HttpServletRequest request) {

	}

	@Override
	protected void customValidateUpdate(final WorkflowConfig domain,
			final BindingResult result, final HttpServletRequest request) {

	}

}
