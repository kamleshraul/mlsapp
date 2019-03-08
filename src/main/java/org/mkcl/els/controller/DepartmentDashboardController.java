package org.mkcl.els.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.DepartmentDashboardVo;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("departmentdashboard")
public class DepartmentDashboardController extends BaseController {
	
	@RequestMapping(value="/getDepartmentDeviceCounts",method=RequestMethod.GET)
	public @ResponseBody List<DepartmentDashboardVo> getDepartmentDeviceCountsWithFilters(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		String sessionType=request.getParameter("session_type");
		String sessionYear=request.getParameter("session_year");
		String houseType = request.getParameter("house_type");
		String subdepartment =request.getParameter("subdepartment");
		String deviceType = request.getParameter("device_type");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				try {
					sessionType=new String(sessionType.getBytes("ISO-8859-1"),"UTF-8");
					sessionYear=new String(sessionYear.getBytes("ISO-8859-1"),"UTF-8");
					houseType=new String(houseType.getBytes("ISO-8859-1"),"UTF-8");
					subdepartment=new String(subdepartment.getBytes("ISO-8859-1"),"UTF-8");
					deviceType=new String(deviceType.getBytes("ISO-8859-1"),"UTF-8");
					
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		List<DepartmentDashboardVo> departmentDeviceCounts = WorkflowDetails.findDepartmentDeviceCountFromWorkflowDetails(sessionType, sessionYear, houseType, deviceType, subdepartment, locale.toString());
		return departmentDeviceCounts;
	}
	
	@RequestMapping(value="/getDepartmentDeviceCountsByHouseType",method=RequestMethod.GET)
	public @ResponseBody List<DepartmentDashboardVo> getDepartmentDeviceCountsByHouseType(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		String sessionType=request.getParameter("session_type");
		String sessionYear=request.getParameter("session_year");
		String houseType = request.getParameter("house_type");
		String subdepartment =request.getParameter("subdepartment");
		String deviceType = request.getParameter("device_type");
		String strStatus=request.getParameter("status");
		
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				try {
					sessionType=new String(sessionType.getBytes("ISO-8859-1"),"UTF-8");
					sessionYear=new String(sessionYear.getBytes("ISO-8859-1"),"UTF-8");
					houseType=new String(houseType.getBytes("ISO-8859-1"),"UTF-8");
					subdepartment=new String(subdepartment.getBytes("ISO-8859-1"),"UTF-8");
					deviceType=new String(deviceType.getBytes("ISO-8859-1"),"UTF-8");
					strStatus=new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		List<DepartmentDashboardVo> departmentDeviceCountsByHouseType = WorkflowDetails.findDepartmentDeviceCountsByHouseTypeFromWorkflowDetails(sessionType, sessionYear, houseType, deviceType, subdepartment,strStatus, locale.toString());
		return departmentDeviceCountsByHouseType;
	}
	
	@RequestMapping(value="/getDepartmentDeviceCountsByDeviceType",method=RequestMethod.GET)
	public @ResponseBody List<DepartmentDashboardVo> getDepartmentDeviceCountsByDeviceType(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){

		String sessionType = request.getParameter("session_type");
		String sessionYear = request.getParameter("session_year");
		String houseType = request.getParameter("house_type");
		String deviceType = request.getParameter("device_type");
		String subdepartment = request.getParameter("subdepartment");
		String strStatus = request.getParameter("status");
		
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				try {
					sessionType=new String(sessionType.getBytes("ISO-8859-1"),"UTF-8");
					sessionYear=new String(sessionYear.getBytes("ISO-8859-1"),"UTF-8");
					houseType=new String(houseType.getBytes("ISO-8859-1"),"UTF-8");
					deviceType=new String(deviceType.getBytes("ISO-8859-1"),"UTF-8");
					subdepartment=new String(subdepartment.getBytes("ISO-8859-1"),"UTF-8");
					strStatus=new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<DepartmentDashboardVo> departmentAssemblyDeviceCountsByDeviceType = WorkflowDetails.findDepartmentAssemblyDeviceCountsByDeviceTypeFromWorkflowDetails(houseType,sessionType, sessionYear,deviceType,subdepartment, strStatus, locale.toString());
		return departmentAssemblyDeviceCountsByDeviceType;
	}
}