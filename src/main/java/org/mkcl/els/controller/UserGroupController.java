package org.mkcl.els.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/usergroup")
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
	}

	@Override
	protected void populateNew(final ModelMap model, final UserGroup domain, final String locale,
			final HttpServletRequest request) {
		/**** Credential ****/
		String strCredential=request.getParameter("credential");
		Credential credential=Credential.findById(Credential.class,Long.parseLong(strCredential));
		domain.setCredential(credential);
		/**** User Group Types ****/
		List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
		model.addAttribute("userGroupTypes",userGroupTypes);
		/**** House Types and Selected House Type ****/
		List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
		model.addAttribute("housetypes",houseTypes);
		if(!houseTypes.isEmpty()){
			model.addAttribute("selectedHouseType",houseTypes.get(0).getName());
		}
		/**** Ministry ****/
		List<Ministry> ministries=Ministry.findAssignedMinistries(locale);
		model.addAttribute("ministries",ministries);
		if(ministries!=null){
			if(!ministries.isEmpty()){
				/**** Departments ****/
				List<Department> departments=MemberMinister.findAssignedDepartments(ministries.get(0), locale);
				model.addAttribute("departments",departments);
				if(departments!=null){
					if(!departments.isEmpty()){
						/**** Sub Departments****/
						List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministries.get(0),departments.get(0), locale);
						model.addAttribute("subDepartments",subDepartments);
					}
				}
			}
		}
		/**** Device Types ****/
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
		model.addAttribute("deviceTypes", deviceTypes);
		/**** Locale ****/
		domain.setLocale(locale);
		model.addAttribute("locale",locale);
		/**** Current Date ****/
		String currentDate=FormaterUtil.getDateFormatter(locale).format(new Date());
		model.addAttribute("currentdate", currentDate);
	}

	@Override
	protected void populateEdit(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
		/**** Locale ****/
		String locale=domain.getLocale();
		model.addAttribute("locale",locale);
		/**** House Types and Selected House Type ****/
		List<HouseType> houseTypes=HouseType.findAllNoExclude("name",ApplicationConstants.ASC, locale);
		model.addAttribute("housetypes",houseTypes);
		String strHouseType=domain.getParameterValue("HOUSETYPE_"+locale).trim();
		HouseType houseType=null;
		if(!houseTypes.isEmpty()){
			if(!strHouseType.isEmpty()){
				model.addAttribute("selectedHouseType",strHouseType);
			}
		}
		/**** Device Types ****/
		List<DeviceType> deviceTypes=DeviceType.findAll(DeviceType.class, "type",ApplicationConstants.ASC, locale);
		model.addAttribute("deviceTypes", deviceTypes);
		model.addAttribute("selectedDeviceType",domain.getParameterValue("DEVICETYPE_"+locale).trim());
		/**** Ministries ****/
		List<Ministry> ministries=Ministry.findAssignedMinistries(locale);
		model.addAttribute("ministries",ministries);		
		String strMinistry=domain.getParameterValue("MINISTRY_"+locale);
		if(strMinistry!=null){
			if(!strMinistry.isEmpty()){
				model.addAttribute("selectedMinistry",strMinistry);
				Ministry selectedMinistry=Ministry.findByName(Ministry.class,strMinistry, locale);
				/**** Departments ****/
				List<Department> departments=MemberMinister.findAssignedDepartments(selectedMinistry, locale);
				model.addAttribute("departments",departments);
				String strDepartment=domain.getParameterValue("DEPARTMENT_"+locale);
				if(strDepartment!=null){
					if(strDepartment.isEmpty()){
						model.addAttribute("selectedDepartment",strDepartment);
						Department selectedDepartment=Department.findByName(Department.class,strDepartment, locale);
						/**** Sub Departments ****/
						List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(selectedMinistry, selectedDepartment, locale);
						model.addAttribute("subdepartments", subDepartments);
						String strSubDepartment=domain.getParameterValue("SUBDEPARTMENT_"+locale);
						model.addAttribute("selectedDepartment",strSubDepartment);
					}
				}
			}
		}
		/**** Current Date ****/
		String currentDate=FormaterUtil.getDateFormatter(locale).format(new Date());
		model.addAttribute("currentdate", currentDate);
		/**** User Group Types ****/
		List<UserGroupType> userGroupTypes=UserGroupType.findAll(UserGroupType.class,"name",ApplicationConstants.ASC, locale);
		model.addAttribute("userGroupTypes",userGroupTypes);
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
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
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final UserGroup domain,
			final HttpServletRequest request) {
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
}
